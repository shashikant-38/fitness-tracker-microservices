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

    private final ActivityRepository activityRepository;
    private final UserValidationService userValidationService;
    private final KafkaTemplate<String, Activity> kafkaTemplate;

    @Value("${kafka.topic.name}")
    private String topicName;

    public ActivityResponse trackActivity(ActivityRequest request) {

        try {
            log.info("===== START: trackActivity =====");
            log.info("Received activity request: {}", request);

            // 🔍 Step 1: Validate user
            log.info("Calling user validation service...");
            boolean isValidUser = userValidationService.validateUser(request.getUserId());
            log.info("User validation result: {}", isValidUser);

            if (!isValidUser) {
                log.error("Invalid user: {}", request.getUserId());
                throw new RuntimeException("Invalid User: " + request.getUserId());
            }

            //  Step 2: Build entity
            Activity activity = Activity.builder()
                    .userId(request.getUserId())
                    .type(request.getType())
                    .duration(request.getDuration())
                    .caloriesBurned(request.getCaloriesBurned())
                    .startTime(request.getStartTime())
                    .additionalMetrics(request.getAdditionalMetrics())
                    .build();

            log.info("Saving activity to MongoDB...");

            //  Step 3: Save to DB
            Activity savedActivity = activityRepository.save(activity);

            log.info("Activity saved successfully. ID: {}", savedActivity.getId());

            //  Step 4: Send to Kafka (non-blocking)
            try {
                log.info("Sending activity to Kafka topic: {}", topicName);

                kafkaTemplate.send(topicName, savedActivity.getUserId(), savedActivity);

                log.info("Kafka message sent successfully");

            } catch (Exception kafkaException) {
                //  Do NOT fail API because of Kafka
                log.error("Kafka send failed, but continuing...", kafkaException);
            }

            //  Step 5: Return response
            ActivityResponse response = mapToResponse(savedActivity);

            log.info("Returning response: {}", response);
            log.info("===== END: trackActivity =====");

            return response;

        } catch (Exception e) {
            log.error("🔥 ERROR in trackActivity()", e);
            throw e;
        }
    }

    // 🔁 Mapper method
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