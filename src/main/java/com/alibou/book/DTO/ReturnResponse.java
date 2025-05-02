package com.alibou.book.DTO;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReturnResponse {
    private Long id;
    private Long orderId;
    private String reason;
    private List<ReturnItemResponse> items;
    private String status;
    private LocalDateTime createdAt;

    // Getters and setters
}