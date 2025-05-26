package com.alibou.book.DTO.FarmersOrdersDTO;

import com.alibou.book.Entity.OrderStatus;
import com.alibou.book.Entity.OrdersStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long orderId;
    private LocalDateTime orderDate;
    private String customerName;
    private OrdersStatus status;
    private BigDecimal totalAmount;
    private List<OrderItemDTO> items;
}