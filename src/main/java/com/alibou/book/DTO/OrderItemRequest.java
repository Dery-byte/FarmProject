package com.alibou.book.DTO;

import lombok.Data;

@Data
public class OrderItemRequest {
    private Long productId;
    private int quantity;
}