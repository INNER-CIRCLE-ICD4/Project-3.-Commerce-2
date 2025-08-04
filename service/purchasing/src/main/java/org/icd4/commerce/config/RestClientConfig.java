package org.icd4.commerce.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * REST 클라이언트 설정.
 */
@Configuration
public class RestClientConfig {
    
    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder()
            .requestFactory(clientHttpRequestFactory())
            .defaultHeader("Accept", "application/json")
            .defaultHeader("Content-Type", "application/json");
    }
    
    @Bean
    public org.springframework.http.client.SimpleClientHttpRequestFactory clientHttpRequestFactory() {
        var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(5));
        factory.setReadTimeout(Duration.ofSeconds(10));
        return factory;
    }
}