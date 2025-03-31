package com.alibou.book.DTO;
import lombok.Data;

@Data
public class AddToCartRequest {
    private Long productId;
    private int quantity;
}
