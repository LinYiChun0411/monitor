package com.aiinspector.config;


import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class RestTemplateConfig {

    /**
     * using pool
     * @return
     */
    @Bean
    public RestTemplate restTemplate() {
      return new RestTemplate(httpRequestFactory());
    }

    @Bean
    public ClientHttpRequestFactory httpRequestFactory() {
      return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    @Bean
    public HttpClient httpClient() {
      Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
          .register("http", PlainConnectionSocketFactory.getSocketFactory())
          .register("https", SSLConnectionSocketFactory.getSocketFactory())
          .build();
      PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);

      connectionManager.setMaxTotal(300);// pool size
      connectionManager.setDefaultMaxPerRoute(50);
      RequestConfig requestConfig = RequestConfig.custom()
										          .setSocketTimeout(10 * 1000) //read timeout
										          .setConnectTimeout(5 * 1000)//connection timeout
										          .setConnectionRequestTimeout(10 * 1000)//connection request timeout
										          .build();
      return HttpClientBuilder.create()
								          .setDefaultRequestConfig(requestConfig)
								          .setConnectionManager(connectionManager)
								          .build();
    }
}
