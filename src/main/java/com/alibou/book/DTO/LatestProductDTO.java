package com.alibou.book.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class LatestProductDTO {

    private Long id;
    private String productName;
    private String description;
    private Double price;
    private Integer quantity;
    private Double weight;
    private String breed;
    private String healthStatus;
    private String condition;
    private String gender;
    private String age;
    private List<String> imageUrls;
    private String category;
    private String farmName;
    private String farmLocation;
    private String farmerName;
    private String farmerContact;
    private LocalDateTime createdAt;
}
