package com.alibou.book.DTO;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class StatusHistoryDTO {
    private Long id;
    private String status;      // Holds the status (APPROVED, PROCESSED, etc.)
    private String changedBy;   // Holds the person who made the change
    private LocalDateTime changedAt;
}