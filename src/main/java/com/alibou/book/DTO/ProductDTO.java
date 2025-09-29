package com.alibou.book.DTO;

import com.alibou.book.Entity.GenderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ProductDTO {
    private Long id;
    private String name;
    private Double price;
    private Integer quantity;
    private String category;
    private List<String> imageUrls; // Add this field for images

    private String description;
    private Double weight;
    private String breed;
    private String healthStatus;
    private String condition;
    private GenderStatus gender;
    private String age;
    private LocalDateTime createdAt;
    private FarmDTO farm;     // can be null
    private FarmerDTO farmer; // always present


}