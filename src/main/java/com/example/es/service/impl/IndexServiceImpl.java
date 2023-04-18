package com.example.es.service.impl;

import com.example.es.config.DynamicDataSourceRouteKey;
import com.example.es.config.RestClientComponent;
import com.example.es.mapper.IndexMapper;
import com.example.es.pojo.InternetInfo;
import com.example.es.pojo.User;
import com.example.es.service.IndexService;
import com.example.es.utils.AsynExecutorUtil;
import com.example.es.utils.MyAssert;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author 山沉
 * @公众号 九月的山沉
 * @微信号 Applewith520
 * @个人博客 https://choleen95.github.io/
 * @博客 https://www.cnblogs.com/Choleen/
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
        DynamicDataSourceRouteKey.setRouteKey(type);
        String index = INDEX_PREFIX+type;
        //判断是否存在
        try {
            existsIndex(index,restClient);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        //创建索引
        CreateIndexResponse response = createIndexAndMappings(restClient, index);
        MyAssert.isEmpty(response,"create index fail");
        //异步插入数据
        bulkInsertData(type,index,restClient);
    }

    @Override
    public List<User> queryByEs() {
        SearchRequest request = new SearchRequest();
        SearchSourceBuilder builder = new SearchSourceBuilder();
        WildcardQueryBuilder queryBuilder = QueryBuilders.wildcardQuery("username", "java");
        builder.query(queryBuilder);
        request.source(builder);
        RestHighLevelClient client = transportClient.getClient();
        try {
            SearchResponse search = client.search(request, RequestOptions.DEFAULT);
            SearchHit[] hits = search.getHits().getHits();
            for (SearchHit hit : hits) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void existsIndex(String index,RestHighLevelClient restClient)throws Exception{
        GetIndexRequest request = new GetIndexRequest(index);
        boolean exists = restClient.indices().exists(request, RequestOptions.DEFAULT);
        if(exists){
            //删除
            DeleteIndexRequest deleteRequest = new DeleteIndexRequest(index);
            restClient.indices().delete(deleteRequest,RequestOptions.DEFAULT);
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
                "        \"type\": \"keyword\"\n" +
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
        double pointNumber = (double)total/1000;
        if(longNumber < pointNumber){
            longNumber++;
        }
        for (Integer i = 0; i < longNumber; i++) {
            Integer finalLongNumber = i*1000;
            AsynExecutorUtil.getPool().execute(()->{
                long start = System.currentTimeMillis();
                logger.info("数据源--->{},线程开始--->{},页数{}",type,Thread.currentThread().getName(),finalLongNumber);
                DynamicDataSourceRouteKey.setRouteKey(type);
                List<InternetInfo> internetInfos = indexMapper.queryInternetInfoList(finalLongNumber, 1000);
                //创建索引
                BulkRequest bulkRequest = new BulkRequest();
                for (InternetInfo entity : internetInfos) {

                    IndexRequest request = new IndexRequest(index);
                    request.id(String.valueOf(entity.getId()));
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        String json = mapper.writeValueAsString(entity);
                        request.source(json,XContentType.JSON);
                        bulkRequest.add(request);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //立即刷新
                bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
                try {
                    BulkResponse bulkItemResponses = restClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                    BulkItemResponse[] items = bulkItemResponses.getItems();
                    BulkItemResponse item = items[0];
                    BulkItemResponse.Failure failure = item.getFailure();
                    if(failure != null){
                        Exception cause = failure.getCause();
                        logger.error(cause.getMessage());
                    }
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
