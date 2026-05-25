package com.springboot.recommendation.controller;

import com.springboot.recommendation.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*")
public class RecommendationController {
    
    @Autowired
    private RecommendationService recommendationService;
    
    @GetMapping
    public ResponseEntity<List<RecommendationService.ProductDTO>> getRecommendations(
            @RequestParam String userEmail,
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(recommendationService.getRecommendations(userEmail, category));
    }
}
