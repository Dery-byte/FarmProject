package com.alibou.book.DTO;

import com.alibou.book.Entity.PaymentMethod;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Data
public class PlaceOrderRequest {
    private Long userId;
    private List<OrderItemRequest> items;
    private PaymentMethod paymentMethod;
    private DeliveryInfoRequest delivery;

}