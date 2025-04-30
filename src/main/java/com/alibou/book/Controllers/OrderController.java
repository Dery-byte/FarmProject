package com.alibou.book.Controllers;

import com.alibou.book.DTO.PlaceOrderRequest;
import com.alibou.book.Entity.Order;
import com.alibou.book.Services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/auth/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @PostMapping("/placeOrder")
    public ResponseEntity<Order> checkout(Principal principal) {
        Order order = orderService.placeOrder(principal);
        return ResponseEntity.ok(order);
    }


    @GetMapping("/allOrders")
    public List<Order> getOrdersByUser(Principal principal) {
        return orderService.getOrdersByUserId(principal);
    }

}
