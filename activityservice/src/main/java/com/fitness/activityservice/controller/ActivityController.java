package com.fitness.activityservice.controller;

import com.fitness.activityservice.ActivityRepository;
import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.service.ActivityService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/activities")
@AllArgsConstructor
@Slf4j
public class ActivityController {

    private ActivityService activityService;
    private ActivityRepository activityRepository;
    private MongoTemplate mongoTemplate;

    @PostMapping
    public ResponseEntity<ActivityResponse> trackactivity(@RequestBody ActivityRequest request){
        log.info("POST /api/activities called with request: {}", request);
        ActivityResponse response = activityService.trackActivity(request);
        log.info("Returning response: {}", response);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<Activity>> getAllActivities(){
        log.info("GET /api/activities called - fetching all activities");
        List<Activity> activities = activityRepository.findAll();
        log.info("Found {} activities in database", activities.size());
        return ResponseEntity.ok(activities);
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> getActivityCount(){
        long count = activityRepository.count();
        log.info("Total activities in database: {}", count);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/test-db")
    public ResponseEntity<Object> testDatabase(){
        long count = activityRepository.count();
        List<Activity> allActivities = activityRepository.findAll();
        log.info("Database test - Count: {}, Activities: {}", count, allActivities);

        String database = mongoTemplate.getDb().getName();
        String collection = mongoTemplate.getCollectionName(Activity.class);

        return ResponseEntity.ok(Map.of(
            "database", database,
            "collection", collection,
            "count", count,
            "activities", allActivities
        ));
    }
}
