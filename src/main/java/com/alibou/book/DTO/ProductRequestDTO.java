package com.alibou.book.DTO;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProductRequestDTO {
    private String productName;
    private String description;
    private Double price;
    private Integer quantity;
    private String category;
    private Long farmId;
    private MultipartFile image; // 👈 Image file
}
