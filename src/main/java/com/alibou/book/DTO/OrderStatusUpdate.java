package com.alibou.book.DTO;

import com.alibou.book.Entity.OrderDetailStatus;
import com.alibou.book.Entity.OrderedItemStatus;
import com.alibou.book.Entity.OrdersStatus;

public class OrderStatusUpdate {

    private OrdersStatus status;
    private OrderedItemStatus itemStatus;
    private OrderDetailStatus orderDetailStatus;


    public OrderedItemStatus getItemStatus() {
        return itemStatus;
    }

    public OrderDetailStatus getOrderDetailStatus() {
        return orderDetailStatus;
    }

    public void setOrderDetailStatus(OrderDetailStatus orderDetailStatus) {
        this.orderDetailStatus = orderDetailStatus;
    }

    public void setItemStatus(OrderedItemStatus itemStatus) {
        this.itemStatus = itemStatus;
    }

    public OrdersStatus getStatus() {
        return status;
    }

    public void setStatus(OrdersStatus status) {
        this.status = status;
    }
}
