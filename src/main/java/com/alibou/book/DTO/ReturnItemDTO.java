package com.alibou.book.DTO;

import com.alibou.book.Entity.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Builder
@Data
public class ReturnItemDTO {
    private Long id;
    //private Long productId;
    private String name;
    private String reason;
    private String quantity;
    private String rejectionReason;
    private LocalDateTime processedDate;
    private ReturnItemStatus status;
    private String currentStatus;

    private String image;
    private List<StatusHistory> statusHistory;

    // Add order details you need from ReturnRequest
    private Long orderId;
    private String overallReason;
    private ReturnStatus requestStatus;

    // Constructor that takes ReturnItem and populates fields
//    public ReturnItemDto(ReturnItem item) {
//        this.id = item.getId();
//        this.productId = item.getProductId();
//        // ... map other fields
//
//        // Map fields from ReturnRequest
//        ReturnRequest request = item.getReturnRequest();
//        this.orderId = request.getOrderId();
//        this.overallReason = request.getReason();
//        this.requestStatus = request.getStatus();
//    }

    // Getters and setters
}