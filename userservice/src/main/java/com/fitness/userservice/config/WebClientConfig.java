package com.fitness.userservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class WebClientConfig {

    /**
     * WebClient for calli  ng the User Service itself via service name (through Eureka / load balancer).
     * If you are not using Eureka, change the baseUrl to the actual host:port, e.g. http://localhost:8080.
     */
    @Bean
    @LoadBalanced
    public WebClient userServiceWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://user-service")
                .build();
    }
}

