package com.example.es.service.impl;

import com.example.es.config.DynamicDataSourceContextHolder;
import com.example.es.config.RestClientComponent;
import com.example.es.mapper.IndexMapper;
import com.example.es.pojo.InternetInfo;
import com.example.es.pojo.User;
import com.example.es.service.IndexService;
import com.example.es.utils.AsynExecutorUtil;
import com.example.es.utils.MyAssert;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import java.io.IOException;
import java.util.List;

/**
 * @author Choleen
 * @since 2020/12/25 22:52
 **/
@Service
public class IndexServiceImpl implements IndexService {
    private static final Logger logger = LoggerFactory.getLogger(IndexServiceImpl.class);

    @Resource
    private IndexMapper indexMapper;

    @Resource
    private RestClientComponent transportClient;

    private static final String INDEX_PREFIX = "cho_index_net_info_";

    @Override
    public List<User> queryUser(){
        return indexMapper.selectOne();
    }


    @Override
    public void batchInsert(String type) {

        RestHighLevelClient restClient = transportClient.getClient();
        MyAssert.isEmpty(restClient,"Elastic restClient is null");
        MyAssert.isEmpty(type,"索引为空");
        DynamicDataSourceContextHolder.setRouteKey(type);
        String index = INDEX_PREFIX+type;
        //判断是否存在
        existsIndex(index,restClient);
        //创建索引
        CreateIndexResponse response = createIndexAndMappings(restClient, index);
        MyAssert.isEmpty(response,"create index fail");
        //异步插入数据
        bulkInsertData(type,index,restClient);
    }

    public void existsIndex(String index,RestHighLevelClient restClient){
        GetIndexRequest request = new GetIndexRequest(index);
        try {
            boolean exists = restClient.indices().exists(request, RequestOptions.DEFAULT);
            if(exists){
                //删除
                DeleteIndexRequest deleteRequest = new DeleteIndexRequest(index);
                restClient.indices().delete(deleteRequest,RequestOptions.DEFAULT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public CreateIndexResponse createIndexAndMappings(RestHighLevelClient restClient,String index){
        CreateIndexRequest indexRequest = new CreateIndexRequest(index);
        indexRequest.settings(Settings.builder()
                .put("index.number_of_shards", 5)
                .put("index.number_of_replicas", 1)
        );
        //创建映射
        String json = " {\n" +
                "    \"properties\": {\n" +
                "      \"title\":{\n" +
                "        \"type\": \"text\",\n" +
                "        \"analyzer\": \"ik_max_word\"\n" +
                "      },\n" +
                "      \"content\":{\n" +
                "        \"type\":\"text\",\n" +
                "        \"analyzer\": \"ik_max_word\"\n" +
                "      },\n" +
                "      \"url\":{\n" +
                "        \"type\": \"text\",\n" +
                "        \"index\": false\n" +
                "      },\n" +
                "      \"date\":{\n" +
                "        \"type\": \"date\",\n" +
                "        \"index\":false\n" +
                "      },\n" +
                "      \"id\":{\n" +
                "        \"type\": \"keyword\"\n" +
                "      }\n" +
                "    }\n" +
                "  }";
        indexRequest.mapping(json, XContentType.JSON);
        indexRequest.alias(new Alias("net_info"));
        indexRequest.setTimeout(TimeValue.timeValueSeconds(10));
        try {
            CreateIndexResponse response = restClient.indices().create(indexRequest, RequestOptions.DEFAULT);
            boolean acknowledged = response.isAcknowledged();
            boolean shardsAcknowledged = response.isShardsAcknowledged();
            if(acknowledged) {
                //所有节点已确认请求
                return response;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public void bulkInsertData(String type,String index,RestHighLevelClient restClient){
        //查询数据
        Integer total = indexMapper.queryCountByInternetInfo();
        //每一千条，一个线程
        Integer longNumber = total/1000;
        double pointNumber = total/1000;
        if(longNumber < pointNumber){
            longNumber++;
        }
        for (long i = 0; i < longNumber; i++) {
            Integer finalLongNumber = longNumber;
            AsynExecutorUtil.getPool().execute(()->{
                long start = System.currentTimeMillis();
                logger.info("数据源--->{},线程开始--->{}",type,Thread.currentThread().getName());
                DynamicDataSourceContextHolder.setRouteKey(type);
                List<InternetInfo> internetInfos = indexMapper.queryInternetInfoList(finalLongNumber, 1000);
                //创建索引
                BulkRequest bulkRequest = new BulkRequest();
                for (InternetInfo entity : internetInfos) {
                    IndexRequest request = new IndexRequest(index);
                    request.id(String.valueOf(entity.getId()));
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        String json = mapper.writeValueAsString(entity);
                        request.source(json);
                        bulkRequest.add(request);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
                //立即刷新
                bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
                try {
                    BulkResponse response = restClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                long end = System.currentTimeMillis();
                long mill = end-start;
                logger.info("{}---->完成---{}",Thread.currentThread().getName(),mill);
            });
        }

    }


}
