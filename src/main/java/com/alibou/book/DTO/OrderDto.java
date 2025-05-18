package com.alibou.book.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
public class OrderDto {
    private Long id;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private Long customerId; // Instead of Customer object
    private List<OrderItemDto> items;
    // getters and setters
}