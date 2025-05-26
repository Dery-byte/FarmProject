package com.alibou.book.DTO.FarmersOrdersDTO;

import com.alibou.book.Entity.OrdersStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Setter
@Getter
public class OrderItemDTO {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal priceAtPurchase;
    private BigDecimal total;
    private OrdersStatus status;

}