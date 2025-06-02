package com.alibou.book.Services;

import com.alibou.book.Entity.Order;
import com.alibou.book.Entity.OrderDetails;
import com.alibou.book.Repositories.OrderDetailsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderDetailsService {

    private final OrderDetailsRepository orderDetailsRepository;

    public OrderDetailsService(OrderDetailsRepository orderDetailsRepository) {
        this.orderDetailsRepository = orderDetailsRepository;
    }


    public ResponseEntity<OrderDetails> getOrderDetails(Long orderId) {
        OrderDetails order = orderDetailsRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return ResponseEntity.ok(order);
    }
}
