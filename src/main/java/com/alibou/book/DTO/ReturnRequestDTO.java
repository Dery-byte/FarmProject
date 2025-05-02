package com.alibou.book.DTO;

import lombok.Data;

import java.util.List;

@Data
public class ReturnRequestDTO {
    private Long orderId;
    private String reason;
    private List<ReturnItemDTO> items;

    // Getters and setters
}