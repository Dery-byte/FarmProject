package com.alibou.book.DTO;

import com.alibou.book.Entity.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;


@Data
@Setter
@Getter
public class OrderItemDto {
    private Long OrderDetailsId;
    private Integer quantity;
    private BigDecimal price;
    private String productName;
private Long productId;
    private BigDecimal total;
    private OrderedItemStatus orderedItemStatus;
    private List<OrderStatusHistory> orderStatusHistoryList;

    private  List<OrderedItemStatusHistory> orderedItemStatusHistoryList;
    private OrderStatus orderStatus;


    // getters and setters
}