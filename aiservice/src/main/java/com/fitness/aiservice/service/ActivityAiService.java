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
import org.apache.catalina.LifecycleState;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.stereotype.Service;

import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

                JsonNode analysisJson =mapper.readTree(jsonContent);
                JsonNode analysisNode = analysisJson.path("analysis");
                StringBuilder fullAnalysis=new StringBuilder();
                addAnalysisSecction(fullAnalysis,analysisNode,"overall","Overall:");
                addAnalysisSecction(fullAnalysis,analysisNode,"pace","Pace:");
                addAnalysisSecction(fullAnalysis,analysisNode,"heartRate","Heart Rate:");
                addAnalysisSecction(fullAnalysis,analysisNode,"caloriesBurned","Calories:");

                List<String> improvements= extractImprovements (analysisJson.path("improvements"));
                List<String> suggestion= extractSuggestions (analysisJson.path("suggestions"));
                List<String> safety= extractSafety (analysisJson.path("Safety"));

            }catch(Exception e){

            }
            return null;
    }

    private List<String> extractSafety(JsonNode safetyNode) {
        List<String> safety = new ArrayList<>();

        if (safetyNode.isArray()) {
            safetyNode.forEach(item -> safety.add(item.asText()));
        }

        return safety.isEmpty() ?
                Collections.singletonList("follow generic guidelines"):
                safety;
    }

    private List<String> extractSuggestions(JsonNode suggestionsNode) {
        List<String> suggesstions= new ArrayList<>();

        if(suggestionsNode.isArray()){
            suggestionsNode.forEach(suggession ->{
                String workout =suggession.path("workout").asText();
                String description =suggession.path("description ").asText();
                suggesstions.add(String.format("%s: %s",workout,description));
            });
        }
        return suggesstions.isEmpty() ?
                Collections.singletonList("No specific suggesions provided"):
                suggesstions;


    }

    private List<String> extractImprovements(JsonNode improvementsNode) {
        List<String> improvements = new ArrayList<>();
        if(improvementsNode.isArray()){
            improvementsNode.forEach(improvement ->{
                String area =improvement.path("area").asText();
                String detail =improvement.path("recommendation ").asText();
                improvements.add(String.format("%s: %s",area,detail));
            });
        }
        return improvements.isEmpty() ?
                Collections.singletonList("No specific improvements provided"):
                improvements;
    }

    private void addAnalysisSecction(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        if(!analysisNode.path(key).isMissingNode()){
            fullAnalysis.append(prefix)
                    .append(analysisNode.path(key).asText())
                    .append("\n\n");
        }
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
