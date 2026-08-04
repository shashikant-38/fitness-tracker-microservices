package com.fitness.aiservice.service;

import com.fitness.activityservice.model.Activity;  // IMPORTANT
import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {


    private final ActivityAiService activityAiService;
    private final RecommendationRepository recommendationRepository;
    @KafkaListener(
            topics = "${kafka.topic.name}",
            groupId = "activity-processor-group"
    )
    public void processActivity(Activity activity) {
        log.info(" Activity received in AI service");
        log.info("UserId: {}", activity.getUserId());
        log.info("Type: {}", activity.getType());
        log.info("Calories: {}", activity.getCaloriesBurned());
         Recommendation recommendation = activityAiService.generateRecommendation(activity);
         recommendationRepository.save(recommendation);
    }
}