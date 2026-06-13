package com.bankingsystem.bankingsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    // RestTemplate bean for calling external services
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // RestClient bean for HTTP calls to microservices (lightweight, no WebFlux needed)
    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}
