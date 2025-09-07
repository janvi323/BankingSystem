package com.bankingsystem.bankingsystem.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@FeignClient(name = "credit-service", url = "http://localhost:8081")
public interface CreditScoreClient {

    @GetMapping("/score/{customerId}")
    @CircuitBreaker(name = "creditCB", fallbackMethod = "fallbackScore")
    int getScore(@PathVariable Long customerId);

    default int fallbackScore(Long customerId, Throwable t) {
        return 600;  // Default safe fallback score
    }
}
