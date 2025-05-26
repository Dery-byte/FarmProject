package com.alibou.book.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

}