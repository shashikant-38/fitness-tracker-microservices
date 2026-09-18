package com.fitness.activityservice.service;

import com.fitness.activityservice.ActivityRepository;
import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {

    private final ActivityRepository repository;
    private final UserValidationService userValidator;
    private final KafkaTemplate<String, Activity> kafkaProducer;

    @Value("${kafka.topic.name}")
    private String topicName;

    public ActivityResponse trackActivity(ActivityRequest request) {

        try {
            log.info("===== START: trackActivity =====");
            log.info("Received activity request: {}", request);

            // Step 1: Validate user
            log.info("Calling user validation service...");

            boolean isValidUser = userValidator.validateUser(request.getUserId());

            log.info("User validation result: {}", isValidUser);

            if (!isValidUser) {
                log.error("Invalid user: {}", request.getUserId());
                throw new RuntimeException("Invalid User: " + request.getUserId());
            }

            // Step 2: Build activity entity
            Activity activity = Activity.builder()
                    .userId(request.getUserId())
                    .type(request.getType())
                    .duration(request.getDuration())
                    .caloriesBurned(request.getCaloriesBurned())
                    .startTime(request.getStartTime())
                    .additionalMetrics(request.getAdditionalMetrics())
                    .build();

            // Step 3: Save activity to MongoDB
            log.info("Saving activity to MongoDB...");

            Activity savedActivity = repository.save(activity);

            log.info(
                    "Activity saved successfully. ID: {}",
                    savedActivity.getId()
            );

            // Step 4: Send activity to Kafka
            try {
                log.info(
                        "Sending activity to Kafka topic: {}",
                        topicName
                );

                kafkaProducer.send(
                        topicName,
                        savedActivity.getUserId(),
                        savedActivity
                );

                log.info("Kafka message sent successfully");

            } catch (Exception kafkaException) {
                // Kafka failure should not fail the API request
                log.error(
                        "Kafka send failed, but continuing...",
                        kafkaException
                );
            }

            // Step 5: Convert entity to response
            ActivityResponse response = mapToResponse(savedActivity);

            log.info("Returning response: {}", response);
            log.info("===== END: trackActivity =====");

            return response;

        } catch (Exception exception) {
            log.error("ERROR in trackActivity()", exception);
            throw exception;
        }
    }

    // Mapper method
    private ActivityResponse mapToResponse(Activity activity) {

        ActivityResponse response = new ActivityResponse();

        response.setId(activity.getId());
        response.setUserId(activity.getUserId());
        response.setType(activity.getType());
        response.setDuration(activity.getDuration());
        response.setCaloriesBurned(activity.getCaloriesBurned());
        response.setStartTime(activity.getStartTime());
        response.setAdditionalMetrics(activity.getAdditionalMetrics());
        response.setCreatedAt(activity.getCreatedAt());
        response.setUpdatedAt(activity.getUpdatedAt());

        return response;
    }
}