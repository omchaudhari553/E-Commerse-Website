package com.springboot.recommendation.service;

import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    
    @Autowired
    private OpenAiChatClient chatClient;
    
    @Autowired
    private RestTemplate restTemplate;
    
    public List<ProductDTO> getRecommendations(String userEmail, String category) {
        try {
            // Get all products from Product Service
            String productServiceUrl = "http://product-service/api/products";
            ProductDTO[] products = restTemplate.getForObject(productServiceUrl, ProductDTO[].class);
            
            if (products == null || products.length == 0) {
                return List.of();
            }
            
            // Filter by category if provided
            List<ProductDTO> filteredProducts;
            if (category != null && !category.isEmpty()) {
                filteredProducts = List.of(products).stream()
                        .filter(p -> category.equalsIgnoreCase(p.getCategory()))
                        .collect(Collectors.toList());
            } else {
                filteredProducts = List.of(products);
            }
            
            // Use Spring AI to get personalized recommendations
            String prompt = buildRecommendationPrompt(userEmail, filteredProducts);
            String aiResponse = chatClient.call(prompt);
            
            // Parse AI response and return top recommendations
            return getTopRecommendations(filteredProducts, aiResponse);
            
        } catch (Exception e) {
            // Fallback to simple category-based recommendations
            return getFallbackRecommendations(category);
        }
    }
    
    private String buildRecommendationPrompt(String userEmail, List<ProductDTO> products) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Based on the following products, recommend the top 5 products for a user: ");
        prompt.append(userEmail).append("\n\n");
        
        for (ProductDTO product : products) {
            prompt.append("- ").append(product.getName())
                  .append(" (Category: ").append(product.getCategory())
                  .append(", Price: ").append(product.getPrice())
                  .append(")\n");
        }
        
        prompt.append("\nPlease return only the product names, separated by commas.");
        return prompt.toString();
    }
    
    private List<ProductDTO> getTopRecommendations(List<ProductDTO> products, String aiResponse) {
        // Simple implementation: return first 5 products
        // In production, parse AI response and match with products
        return products.stream()
                .limit(5)
                .collect(Collectors.toList());
    }
    
    private List<ProductDTO> getFallbackRecommendations(String category) {
        try {
            String productServiceUrl = "http://product-service/api/products";
            if (category != null && !category.isEmpty()) {
                productServiceUrl += "/category/" + category;
            }
            
            ProductDTO[] products = restTemplate.getForObject(productServiceUrl, ProductDTO[].class);
            
            if (products == null || products.length == 0) {
                return List.of();
            }
            
            return List.of(products).stream()
                    .limit(5)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            return List.of();
        }
    }
    
    // DTO for Product Service response
    public static class ProductDTO {
        private Long id;
        private String name;
        private String description;
        private String category;
        private String imageUrl;
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }
}
