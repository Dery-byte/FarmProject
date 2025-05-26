package com.alibou.book.DTO;

import com.alibou.book.Entity.DeliveryDetailsDTO;
import com.alibou.book.Entity.OrderStatus;
import com.alibou.book.Entity.OrderSummaryDTO;
import com.alibou.book.Entity.OrdersStatus;
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
    private String customerName;
    private String customerEmail;
    private List<OrderItemDto> items;
    private OrdersStatus status;

    private DeliveryDetailsDTO deliveryDetails;

    private OrderSummaryDTO summary;


    // getters and setters
}