package com.alibou.book.DTO;

import com.alibou.book.Entity.PaymentMethod;
import lombok.Data;

import java.util.List;

@Data
public class PlaceOrderRequest {
    private Long userId;
    private List<OrderItemRequest> items;
    private PaymentMethod paymentMethod;
}