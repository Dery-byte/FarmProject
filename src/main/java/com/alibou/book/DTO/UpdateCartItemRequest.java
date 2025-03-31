package com.alibou.book.DTO;

import lombok.Data;

@Data
public class UpdateCartItemRequest {
    private Long productId;
    private int quantity;
}
