package com.alibou.book.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FarmWithProductsDTO {
    private Long id;
    private String name;
    private List<ProductDTO> products;
}