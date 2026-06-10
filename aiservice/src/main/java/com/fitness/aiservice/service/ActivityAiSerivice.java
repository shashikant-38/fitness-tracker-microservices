package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.service.GeminiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAiSerivice {

    private final GeminiService geminiService;

    public void generateRecommendation(Activity activity){
        String prompt= createPromptForActivity(activity);//create this method .. at below
        String response = geminiService.getRecommendations(prompt);

        log.info("RESPONSE FROM AI {}", response);

    }
}
