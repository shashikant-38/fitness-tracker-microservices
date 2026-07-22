package com.fitness.aiservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.activityservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class ActivityAiService {

     private final GeminiService geminiService;

    public Recommendation generateRecommendation(Activity activity) {
        String prompt = createPromptForActivity(activity);//create this method .. at below
        String airesponse = geminiService.getRecommendations(prompt);

        log.info("RESPONSE FROM AI {}", airesponse);
        return processAIResponse(activity,airesponse);
    }

    private Recommendation processAIResponse(Activity activity, String airesponse) {
            try{
                ObjectMapper mapper =new ObjectMapper();
                JsonNode rootNode=mapper.readTree(airesponse);
                JsonNode textNode=rootNode.path("candidates")
                        .get(0)
                        .path("content")
                        .path(0)
                        .path("text");

                String jsonContent = textNode.asText()
                        .replaceAll("```json\\n","")
                        .replaceAll("\\n```", "")
                        .trim();
//                log.info("RESPONSE FROM CLEANED AI {}", jsonContent);


            }catch(Exception e){

            }
            return null;
    }

    //prompt FOR AI
    private String createPromptForActivity(Activity activity) {
        return String.format("""
        Analyze this fitness activity and provide detailed recommendations in the following EXACT JSON format:
        {
          "analysis": {
            "overall": "Overall analysis here",
            "pace": "Pace analysis here",
            "heartRate": "Heart rate analysis here",
            "caloriesBurned": "Calories analysis here"
          },
          "improvements": [
            {
              "area": "Area name",
              "recommendation": "Detailed recommendation"
            }
          ],
          "suggestions": [
            {
              "workout": "Workout name",
              "description": "Detailed workout description"
            }
          ],
          "safety": [
            "Safety point 1",
            "Safety point 2"
          ]
        }

         Analyze this activity:
         Activity Type: %s
         Duration: %d minutes
         Calories Burned: %d
         Additional Metrics: %s
        
        Provide detailed analysis focusing on performance, improvements, next workout suggestions, and safety guidelines.
        Ensure the response follows the EXACT JSON format shown above.
        """,
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics()
        );
    }
}
