package com.alibou.book.Controllers;

import com.alibou.book.DTO.*;
import com.alibou.book.DTO.FarmersOrdersDTO.OrderDTO;
import com.alibou.book.Entity.Order;
import com.alibou.book.Repositories.Projections.WeeklyRevenueSummary;
import com.alibou.book.Services.OrderService;
import com.alibou.book.user.User;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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








    @GetMapping("/revenueMonthly")
    public ResponseEntity<List<MonthlyRevenueSummary>> getMonthlyRevenue(@RequestParam int year) {
        List<MonthlyRevenueSummary> data = orderService.getMonthlyRevenue(year);
        return ResponseEntity.ok(data);
    }




    @GetMapping("/ordersMonthly")
    public ResponseEntity<List<MonthlyOrderSummary>> getMonthlyOrders(@RequestParam int year) {
        List<MonthlyOrderSummary> data = orderService.getMonthlyOrders(year);
        return ResponseEntity.ok(data);
    }





    //GET ORDERS BY FARMER

    @GetMapping("/allOrdersByFarmer")
    public ResponseEntity<List<OrderDto>> getFarmerOrders(Principal principal) {
        //User currentFarmer = authenticationService.getCurrentUser();
        if (principal == null) {
            throw new IllegalArgumentException("User must be authenticated to view return request.");
        }
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        List<Order> orders = orderService.getOrdersByFarmer(Long.valueOf(user.getId()));

        // Convert to DTOs
        List<OrderDto> dtos = orders.stream()
                .map(order -> orderService.convertToDTO(order, Long.valueOf(user.getId())))
                .collect(Collectors.toList());


        return ResponseEntity.ok(dtos);
    }




    @GetMapping("/paginated")
    public ResponseEntity<Page<OrderDto>> getFarmerOrdersPaginated(Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        //User currentFarmer = authenticationService.getCurrentUser();

        if (principal == null) {
            throw new IllegalArgumentException("User must be authenticated to view return request.");
        }
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        Page<Order> orders = orderService.getOrdersByFarmer(
                Long.valueOf(user.getId()),
                PageRequest.of(page, size, Sort.by("orderDate").descending()));

        Page<OrderDto> dtos = orders.map(order -> orderService.convertToDTO(order, Long.valueOf(user.getId())));
        return ResponseEntity.ok(dtos);
    }


    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrderDetails(Principal principal, @PathVariable Long orderId) {
        //User currentFarmer = authenticationService.getCurrentUser();

        if (principal == null) {
            throw new IllegalArgumentException("User must be authenticated to view return request.");
        }
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        OrderDto orderDto = orderService.getOrderDetails(orderId, Long.valueOf(user.getId()));
        return ResponseEntity.ok(orderDto);
    }









    //ORDER COUNT BY FARMER




    @GetMapping("/farmer/orders")
    public List<Order> getFarmerOrders() {
        return orderService.getOrdersByCurrentFarmer();
    }

    @GetMapping("/farmerOrderCount")
    public long getFarmerOrderCount() {
        return orderService.getOrderCountByCurrentFarmer();
    }

    @GetMapping("/farmerTotalSales")
    public BigDecimal getFarmerTotalSales() {
        return orderService.getTotalSalesByCurrentFarmer();
    }



    // ✅ Monthly summary for authenticated farmer
    @GetMapping("/monthly-summaryForFarmer")
    public ResponseEntity<List<MonthlyOrderSummary>> getMonthlyOrderSummaryByAuthenticatedFarmer(
            @RequestParam int year,
            Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // Get farmer's ID from the principal (assuming you can load it by username)
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        Long farmerId = Long.valueOf(user.getId());

        List<MonthlyOrderSummary> summaries = orderService.getMonthlyOrdersForFarmer(year, farmerId);
        return ResponseEntity.ok(summaries);
    }







    @GetMapping("/monthly-revenueFarmer")
    public ResponseEntity<List<MonthlyRevenueSummary>> getMonthlyRevenueForFarmer(
            @RequestParam int year,
            Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // Get farmer's ID from the principal (assuming you can load it by username)
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        Long farmerId = Long.valueOf(user.getId());

        List<MonthlyRevenueSummary> summaries = orderService.getMonthlyRevenueForFarmer(year,farmerId);
        return ResponseEntity.ok(summaries);
    }







    @GetMapping("/weekly-summaryByFarmer")
    public ResponseEntity<List<WeeklyRevenueSummary>> getWeeklyRevenueForFarmer(
            @RequestParam int year,
            @RequestParam int month,
            Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // Get farmer's ID from the principal (assuming you can load it by username)
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        Long farmerId = Long.valueOf(user.getId());

        List<WeeklyRevenueSummary> summary = orderService.getWeeklyTotalsForFarmer(year, month, farmerId);
        return ResponseEntity.ok(summary);
    }





    // Endpoint: /api/analytics/daily-revenue?year=2024&month=5
    @GetMapping("/daily-summaryByFarmer")
    public List<Map<String, Object>> getDailyRevenue(
            @RequestParam int year,
            @RequestParam int month,
            Principal principal) {
        if (principal == null) {
            return (List<Map<String, Object>>) ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // Get farmer's ID from the principal (assuming you can load it by username)
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        Long farmerId = Long.valueOf(user.getId());
        return orderService.getDailyRevenueForFarmer(year, month, farmerId);
    }

}
