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

    private List<String> extractSafety(JsonNode safety) {
    }

    private List<String> extractSuggestions(JsonNode suggestions) {
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
        return String.format();
    }
}
