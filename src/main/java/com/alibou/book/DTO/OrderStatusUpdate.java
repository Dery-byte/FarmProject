package com.alibou.book.DTO;

import com.alibou.book.Entity.OrdersStatus;

public class OrderStatusUpdate {

    private OrdersStatus status;

    public OrdersStatus getStatus() {
        return status;
    }

    public void setStatus(OrdersStatus status) {
        this.status = status;
    }
}
