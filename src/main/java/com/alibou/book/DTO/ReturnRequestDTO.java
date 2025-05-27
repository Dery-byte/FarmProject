package com.alibou.book.DTO;

import com.alibou.book.Entity.ReturnStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
//@Builder
public class ReturnRequestDTO {
    private Long id;
    private Long orderId;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime requestDate;
    private ReturnStatus status;
    private List<ReturnItemDTO> items;

    // Getters and setters
}