package com.alibou.book.DTO;

import com.alibou.book.Entity.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
public class OrderDto {
    private Long orderId;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private Long customerId; // Instead of Customer object
    private String customerName;
    private String customerEmail;
    private List<OrderItemDto> items;
    private OrdersStatus status;
    private PaymentMethod paymentMethod;
    private boolean isPaid;
    private OrderStatus orderStatus;

    private DeliveryDetailsDTO deliveryDetails;


    private DeliveryDTO delivery;

    private OrderSummaryDTO summary;
    private List<OrderStatusHistory> orderStatusHistoryList;


    // getters and setters
}