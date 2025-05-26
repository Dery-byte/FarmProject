package com.alibou.book.Services.OrderServicesImpl;

import com.alibou.book.DTO.FarmersOrdersDTO.OrderDTO;
import com.alibou.book.Entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    List<Order> getOrdersByFarmer(Long farmerId);
    Page<Order> getOrdersByFarmer(Long farmerId, Pageable pageable);
    OrderDTO getOrderDetails(Long orderId, Long farmerId);
}