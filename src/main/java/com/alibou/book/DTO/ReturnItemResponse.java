package com.alibou.book.DTO;

import lombok.Data;

@Data
public class ReturnItemResponse {
    private Long productId;
    private String name;
    private String reason;

    // Getters and setters
}