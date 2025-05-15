package com.alibou.book.Controllers;

import com.alibou.book.DTO.*;
import com.alibou.book.Entity.Order;
import com.alibou.book.Repositories.Projections.WeeklyRevenueSummary;
import com.alibou.book.Services.OrderService;
import com.alibou.book.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin("*")
@RequestMapping("/auth/order")
public class OrderController {
    private final OrderService orderService;

    private final UserDetailsService userDetailsService;



    public OrderController(OrderService orderService, UserDetailsService userDetailsService) {
        this.orderService = orderService;
        this.userDetailsService = userDetailsService;
    }



    @PostMapping("/placeOrder")
    public ResponseEntity<Order> checkout(Principal principal,@Valid @RequestBody PlaceOrderRequest request ) {
        Order order = orderService.placeOrder(principal,request);
        return ResponseEntity.ok(order);
    }


    @GetMapping("/allOrdersByUser")
    public List<Order> getOrdersByUser(Principal principal) {
        return orderService.getOrdersByUserId(principal);
    }




    @PatchMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody OrderStatusUpdate request, Principal principal) {

        if (principal == null) {
            throw new IllegalArgumentException("User must be authenticated to view return request.");
        }
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());

        try {
            Order updatedOrder = orderService.updateOrderStatus(orderId, request.getStatus(), user.getFullName());
            System.out.println("Order status update to " + request.getStatus());
            return ResponseEntity.ok(updatedOrder);
//            return ResponseEntity.ok("Order status updated to " + request.getStatus());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/allOrders")
    public List<Order> allOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/count")
    public long getOrderCount() {
        return orderService.getTotalOrderCount();
    }


    @GetMapping("/total-sales")
    public BigDecimal getTotalOrdersAmount() {
        return orderService.getTotalOrdersAmount();
    }




















    @GetMapping("/revenueDaily")
    public ResponseEntity<List<Map<String, Object>>> getDailyTotals(
            @RequestParam int year,
            @RequestParam int month) {
        List<Map<String, Object>> result = orderService.getDailyTotals(year, month);
        return ResponseEntity.ok(result);
    }

    // Weekly revenue API
    @GetMapping("/revenueWeekly")
    public ResponseEntity<List<WeeklyRevenueSummary>> getWeeklyTotals(
            @RequestParam int year,
            @RequestParam int month) {
        List<WeeklyRevenueSummary> result = orderService.getWeeklyTotals(year, month);
        return ResponseEntity.ok(result);
    }






}
