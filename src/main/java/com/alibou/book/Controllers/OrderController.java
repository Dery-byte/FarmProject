package com.alibou.book.Controllers;

import com.alibou.book.DTO.ReturnItemResponse;
import com.alibou.book.DTO.ReturnResponse;
import com.alibou.book.Entity.Order;
import com.alibou.book.Entity.ReturnRequest;
import com.alibou.book.Services.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;


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


    @GetMapping("/allOrdersByUser")
    public List<Order> getOrdersByUser(Principal principal) {
        return orderService.getOrdersByUserId(principal);
    }


    @GetMapping("/allOrders")
    public List<Order> allOrders() {
        return orderService.getAllOrders();
    }




}
