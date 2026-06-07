package com.fitness.activityservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public WebClient userServiceWebClient(WebClient.Builder builder) {
        // Service ID of the userservice registered in Eureka (spring.application.name=user-service)
        return builder
                .baseUrl("http://USERSERVICE")
                .build();
    }
}
