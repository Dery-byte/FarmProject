package com.alibou.book.DTO;

import com.alibou.book.Entity.Product;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FarmResponse {
    private Long id;
    private String farmName;
    private String location;
    private List<Product> products;
}