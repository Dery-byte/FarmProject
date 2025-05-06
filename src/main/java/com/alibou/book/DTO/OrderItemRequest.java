package com.alibou.book.DTO;

import com.alibou.book.Entity.StatusHistory;
import lombok.Data;

import java.util.List;

@Data
public class OrderItemRequest {
    private Long productId;
    private int quantity;
//    private List<ReturnItemResponse> returnItemResponses;

}