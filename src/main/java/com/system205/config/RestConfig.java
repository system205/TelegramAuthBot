package com.system205.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class RestConfig {

    @Bean
    public WebClient webClient(@Value("${web-client.base-url:http://localhost:8080}") String baseUrl) {
        return WebClient.create(baseUrl);
    }
}
