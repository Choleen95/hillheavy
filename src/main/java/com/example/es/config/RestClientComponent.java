package com.example.es.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.jcp.xml.dsig.internal.dom.ApacheOctetStreamData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.persistence.criteria.CriteriaBuilder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Elastic链接
 * @author 山沉
 * @公众号 九月的山沉
 * @微信号 Applewith520
 * @个人博客 https://choleen95.github.io/
 * @博客 https://www.cnblogs.com/Choleen/
 * @since 2021/1/7 23:25
 */
@Component
public class RestClientComponent {
    private static final Logger logger = LoggerFactory.getLogger(RestClientComponent.class);

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
                                      builder.setSSLHostnameVerifier(new HostnameVerifier() {
                                          @Override
                                          public boolean verify(String s, SSLSession sslSession) {
                                              String host = sslSession.getPeerHost();
                                              if(s.equals(host)){
                                                  return true;
                                              }
                                              logger.info("host not same");
                                              return false;
                                          }
                                      });
                                      //信任所有证书
                                      SSLContext context = null;
                                        try {
                                            context = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                                                @Override
                                                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                                                    return true;
                                                }
                                            }).build();
                                        } catch (NoSuchAlgorithmException e) {
                                            e.printStackTrace();
                                        } catch (KeyManagementException e) {
                                            e.printStackTrace();
                                        } catch (KeyStoreException e) {
                                            e.printStackTrace();
                                        }
                                        builder.setDefaultCredentialsProvider(credentialsProvider);
                                      builder.setMaxConnTotal(100);
                                      builder.setSSLContext(context);
                                        return builder;
                                    }
                            })
            );

        }
        return restClient;
    }
}
