package com.bankingsystem.bankingsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    // RestTemplate bean for calling external services
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // WebClient bean for reactive HTTP calls
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
