package com.bankingsystem.bankingsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

/**
 * Configuration for REST client used for external API calls
 */
@Configuration
public class RestClientConfig {
    
    /**
     * RestTemplate bean for making HTTP requests to AI APIs
     */
    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory clientHttpRequestFactory) {
        return new RestTemplate(clientHttpRequestFactory);
    }
    
    /**
     * HTTP request factory with timeout configurations
     */
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        
        // Connection timeout: 10 seconds
        factory.setConnectTimeout(10000);
        
        // Read timeout: 30 seconds (AI APIs can take time to respond)
        factory.setReadTimeout(30000);
        
        return factory;
    }
}
