package com.alibou.book.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Data
@Setter
@Getter
public class OrderItemDto {
    private Long id;
    private Integer quantity;
    private BigDecimal price;
    private Long productId;
    // getters and setters
}