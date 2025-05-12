package com.alibou.book.DTO;

import com.alibou.book.Entity.GenderStatus;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List; // ⬅️ Add this import

@Data
public class ProductRequestDTO {
    private String productName;
    private String description;
    private Double price;
    private Integer quantity;
    private String category;
    private Long farmId;
    private Double weight;
    private String breed;
    private String healthStatus;
    private String condition;
    private GenderStatus gender;
    private String age;  // IN months
    private List<MultipartFile> images; // ⬅️ NOW multiple images
}
