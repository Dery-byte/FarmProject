package com.alibou.book.Entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderSummaryDTO {
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private String currency = "GHS"; // Default to GHS
}