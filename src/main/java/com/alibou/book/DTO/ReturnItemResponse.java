package com.alibou.book.DTO;

import com.alibou.book.Entity.StatusHistory;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ReturnItemResponse {
    private Long productId;
    private String name;
    private String reason;
   // private List<String> imageUrls; // Add this field for images
    private List<StatusHistory> historyList;


    // Getters and setters
}