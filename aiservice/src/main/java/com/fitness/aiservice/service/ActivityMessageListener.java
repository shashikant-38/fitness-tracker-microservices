package com.fitness.aiservice.service;

import com.fitness.activityservice.model.Activity;  // IMPORTANT
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {


    private final ActivityAiService activityAiService;
    @KafkaListener(
            topics = "${kafka.topic.name}",
            groupId = "activity-processor-group"
    )
    public void processActivity(Activity activity) {
        log.info(" Activity received in AI service");
        log.info("UserId: {}", activity.getUserId());
        log.info("Type: {}", activity.getType());
        log.info("Calories: {}", activity.getCaloriesBurned());
         activityAiService.generateRecommendation(activity);
    }
}