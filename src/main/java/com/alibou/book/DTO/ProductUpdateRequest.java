package com.alibou.book.DTO;

import lombok.Data;

import java.util.List;

@Data
public class ProductUpdateRequest {
    private String productName;
    private String description;
    private Double price;
    private Integer quantity;
    private String category;
    private Double weight;
    private String breed;
    private String healthStatus;
    private String condition;
    private String age;
    private String gender;
    private List<String> existingImageUrls; // For existing image URLs

    // Exclude new images from DTO - they'll be handled separately
    // Getters and setters
}