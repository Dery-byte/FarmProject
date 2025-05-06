package com.alibou.book.DTO;

import com.alibou.book.Entity.StatusHistory;
import lombok.Data;

import java.util.List;

@Data
public class ReturnItemResponse {
    private Long productId;
    private String name;
    private String reason;
    private List<StatusHistory> historyList;


    // Getters and setters
}