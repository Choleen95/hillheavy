package com.example.es.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.jcp.xml.dsig.internal.dom.ApacheOctetStreamData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;

/**
 * Elastic链接
 * @author 山沉
 * @公众号 九月的山沉
 * @微信号 Applewith520
 * @Github https://github.com/Choleen95
 * @博客 https://www.cnblogs.com/Choleen/
 * @since 2021/1/7 23:25
 */
@Component
public class RestClientComponent {

    @Value("${elasticsearch.username}")
    private String username;
    @Value("${elasticsearch.password}")
    private String password;
    @Value("${elasticsearch.host}")
    private String host;
    @Value("${elasticsearch.port}")
    private Integer port;
    @Value("${DEFAULT:http}")
    private String defaultHttp;

    private static RestHighLevelClient restClient;


    public RestHighLevelClient getClient(){
        if(restClient == null){
            restClient = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost(host, port, defaultHttp))
                            .setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
                                @Override
                                public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder builder) {
                                    builder.setConnectTimeout(5000);
                                    builder.setSocketTimeout(60000);
                                    return builder;
                                }
                            })
                            .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                                @Override
                                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder builder) {
                                    //基础认证
                                    final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                                    credentialsProvider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials(username, password));
                                    builder.setDefaultCredentialsProvider(credentialsProvider);
                                    builder.setMaxConnTotal(100);

                                    return builder;
                                }
                            })
            );

        }
        return restClient;
    }
}
