package com.alibou.book.DTO;

import com.alibou.book.Entity.OrderedItemStatus;
import com.alibou.book.Entity.OrdersStatus;
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
    private String productName;
    //private Long productId;
    private BigDecimal total;
    private OrderedItemStatus orderedItemStatus;

    // getters and setters
}