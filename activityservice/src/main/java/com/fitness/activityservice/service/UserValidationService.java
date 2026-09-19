package com.fitness.activityservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserValidationService {

    private final WebClient.Builder webClientBuilder;

    public boolean validateUser(String userId) {

        log.info("Validating user with ID: {}", userId);

        try {
            Boolean response = webClientBuilder.build()
                    .get()
                    // 🔥 Use localhost for now (safe fix)
                    .uri("http://localhost:8081/api/users/{userId}/validate", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .timeout(Duration.ofSeconds(3))   // ⏱ prevents hanging
                    .onErrorResume(ex -> {
                        log.error("Error while calling User Service: {}", ex.getMessage());
                        return Mono.just(false); // fallback
                    })
                    .block();

            log.info("User validation response: {}", response);

            return Boolean.TRUE.equals(response);

        } catch (Exception e) {
            log.error("User service is unreachable: {}", e.getMessage());
            return false; // 🔥 prevents 500 error
        }
    }
}