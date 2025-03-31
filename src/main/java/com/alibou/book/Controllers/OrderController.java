package com.alibou.book.Controllers;

import com.alibou.book.DTO.PlaceOrderRequest;
import com.alibou.book.Entity.Order;
import com.alibou.book.Services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;


@RestController
@RequestMapping("/auth/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/place-order")
    public ResponseEntity<Order> placeOrder(@RequestBody PlaceOrderRequest request, Principal principal) {
        Order order = orderService.placeOrder(request, principal);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }
}
