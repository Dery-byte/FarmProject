package com.alibou.book.Controllers;


import com.alibou.book.Entity.Order;
import com.alibou.book.Entity.OrderDetails;
import com.alibou.book.Services.OrderDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/auth/orderdetails")
public class OrderDetailsController {

    private OrderDetailsService orderDetailsService;

    public OrderDetailsController(OrderDetailsService orderDetailsService) {
        this.orderDetailsService = orderDetailsService;
    }

//    @GetMapping("/details/{id}")
//    public List<OrderDetails> getOrdersByUser(@PathVariable Long id) {
//        return (List<OrderDetails>) orderDetailsService.getOrderDetails(id);
//    }

    @GetMapping("/details/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable Long orderId) {
        try {
            OrderDetails order = orderDetailsService.getOrderDetails(orderId).getBody();
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", e.getMessage(),
                            "timestamp", LocalDateTime.now()
                    ));
        }
    }

}
