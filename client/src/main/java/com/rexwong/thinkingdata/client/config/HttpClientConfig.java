package com.rexwong.thinkingdata.client.config;

import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


@Configuration
public class HttpClientConfig {

    @Value("${spring.httpclient.timeout.connect}")
    private int connectTimeout;

    @Value("${spring.httpclient.timeout.read}")
    private int readTimeout;

    @Value("${spring.httpclient.connection.pool.maxTotal}")
    private int maxTotal;

    @Value("${spring.httpclient.connection.pool.defaultMaxPerRoute}")
    private int defaultMaxPerRoute;

    @Value("${spring.httpclient.retry.times}")
    private int retryTimes;

    @Bean
    @DependsOn
    RestTemplate getRestTemplate(ClientHttpRequestFactory factory){
        RestTemplate restTemplate= new RestTemplate();
        restTemplate.setRequestFactory(factory);

        return restTemplate;
    }

    @Bean
    ClientHttpRequestFactory getHttpRequestFactory(){
        HttpClientBuilder builder = getHttpClientBuilder();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectionRequestTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        factory.setHttpClient(builder.build());
        return factory;
    }

    private HttpClientBuilder getHttpClientBuilder(){
        HttpClientConnectionManager manager = getConnectionManager();
        DefaultHttpRequestRetryHandler requestRetryHandler = new DefaultHttpRequestRetryHandler(retryTimes, false);
        return HttpClientBuilder.create().setConnectionManager(manager).setRetryHandler(requestRetryHandler);
    }

    private HttpClientConnectionManager getConnectionManager(){
        PoolingHttpClientConnectionManager poolManager = new PoolingHttpClientConnectionManager();
        poolManager.setMaxTotal(maxTotal);
        poolManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        return poolManager;
    }

}
