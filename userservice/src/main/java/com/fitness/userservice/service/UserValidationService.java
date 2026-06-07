package com.fitness.userservice.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserValidationService {
    private final WebClient userServiceWebClient;

    public boolean validateUser(String userId){
log.info("Cslling User Service for {}",userId);
        try{
            return userServiceWebClient.get()
                    .uri("/api/users/{userId}/validate",userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();

        }catch(WebClientResponseException e){
            if (e.getStatusCode().value() == 404) {
                throw new RuntimeException("User Not Found: " + userId);
            }
            else if (e.getStatusCode().value() == 400) {
                throw new RuntimeException("Invalid Request: " + userId);
            }
            else {
                throw new RuntimeException("Service Error: " + e.getMessage());
            }
        }
    }
}
