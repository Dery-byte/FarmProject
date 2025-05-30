package com.alibou.book.Services;
import com.alibou.book.DTO.*;
import com.alibou.book.DTO.FarmersOrdersDTO.OrderDTO;
import com.alibou.book.DTO.FarmersOrdersDTO.OrderItemDTO;
import com.alibou.book.Entity.*;
import com.alibou.book.Repositories.CartRepository;
import com.alibou.book.Repositories.OrderRepository;
import com.alibou.book.Repositories.PaymentRepository;
import com.alibou.book.Repositories.ProductRepository;
import com.alibou.book.Repositories.Projections.WeeklyRevenueSummary;
import com.alibou.book.exception.ResourceNotFoundException;
import com.alibou.book.exception.UnauthorizedAccessException;
import com.alibou.book.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.context.DelegatingApplicationListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    private final UserDetailsService userDetailsService;
    private final CartService cartService;
    private final CartRepository cartRepository;
    private final DelegatingApplicationListener delegatingApplicationListener;
    private final PaymentRepository paymentRepository;
//    private final UserRepository userRepository;
//@Transactional
//public Order placeOrder(Principal principal) {
//    Cart cart = cartService.getUserCart(principal);
//
//    if (cart.getItems().isEmpty()) {
//        throw new RuntimeException("Cart is empty");
//    }
//
//    Order order = new Order();
//    order.setOrderDate(LocalDateTime.now());
//    order.setCustomer(cart.getUser());
//    order.setAmount(cartService.getCartTotal(cart));
//    order.setOrdersStatus(OrdersStatus.PENDING);
//    order.setStatus(PENDING);
//    order.setPaid(false); // until payment is made
//
//    List<OrderDetails> orderDetailsList = new ArrayList<>();
//
//    for (CartItem item : cart.getItems()) {
//        OrderDetails detail = new OrderDetails();
//        detail.setOrder(order);
//        detail.setProduct(item.getProduct());
//        detail.setQuantity(item.getQuantity());
//        detail.setPrice(item.getPrice());
//        orderDetailsList.add(detail);
//        // Update product quantity
//        Product product = item.getProduct();
//        product.setQuantity(product.getQuantity() - item.getQuantity());
//        productRepository.save(product);
//    }
//
//    order.setOrderDetails(orderDetailsList);
//    orderRepository.save(order);
//    // Clear the cart
//    cart.getItems().clear();
//    cartRepository.save(cart);
//
//    return order;
//}


    @Transactional
    public Order placeOrder(Principal principal, PlaceOrderRequest request) {
        // 1. Get user cart
        Cart cart = cartService.getUserCart(principal);
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot place order: Cart is empty");
        }

        // 2. Validate and create delivery info
        Delivery deliveryInfo = new Delivery(
                request.getDelivery().getRecipientName(),
                request.getDelivery().getPhoneNumber(),
                request.getDelivery().getDigitalAddress(),
                request.getDelivery().getArea(),
                request.getDelivery().getDistrict(),
                request.getDelivery().getNotes(),
                request.getDelivery().getLandmark(),
                request.getDelivery().getStreet(),
                request.getDelivery().getRegion()
        );
        deliveryInfo.validate(); // Throws IllegalArgumentException if invalid


        // Create Payment during Order Processing



        // 3. Create order with delivery info
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setCustomer(cart.getUser());
        order.setAmount(cartService.getCartTotal(cart));
        order.setOrdersStatus(OrdersStatus.PENDING);
        order.setStatus(OrderStatus.PENDING);
        order.setPaid(false);
        order.setDeliveryInfo(deliveryInfo); // Embedded delivery details


        Payment payment = new Payment();
        payment.setPaymentDate(LocalDateTime.now());
        payment.setAmount(cartService.getCartTotal(cart));
        payment.setPaymentMethod(PaymentMethod.MOBILE_MONEY);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionId("999987363536839");
        payment.setOrder(order);
        paymentRepository.save(payment);

        // 4. Convert cart items to order details
        List<OrderDetails> orderDetailsList = cart.getItems().stream()
                .map(item -> {
                    OrderDetails detail = new OrderDetails();
                    detail.setOrder(order);
                    detail.setProduct(item.getProduct());
                    detail.setQuantity(item.getQuantity());
                    detail.setPrice(item.getPrice());

                    // Update product stock
                    Product product = item.getProduct();
                    product.setQuantity(product.getQuantity() - item.getQuantity());
                    productRepository.save(product);

                    return detail;
                })
                .toList();

        order.setOrderDetails(orderDetailsList);

        // 5. Save and clear cart
        Order savedOrder = orderRepository.save(order);
        cart.getItems().clear();
        return savedOrder;
    }






    public List<Order> getOrdersByUserId(Principal principal) {
        if (principal == null) {
            throw new IllegalArgumentException("User must be authenticated to fetch orders.");
        }
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
//        return orderRepository.findByUserId(Long.valueOf(user.getId()));
        return orderRepository.findByCustomer_Id(Long.valueOf(user.getId()));
    }


    public List<Order> getAllOrders(){
    return orderRepository.findAll();
    }

    public long getTotalOrderCount() {
        return orderRepository.count();
    }


    public BigDecimal getTotalOrdersAmount() {
        return orderRepository.getTotalAmountSafe();
    }


















//TRYING THE ORDERS PROGRESS UPDATE


    public Order updateOrderStatus(Long orderId, OrdersStatus newStatus, String adminUsername) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        OrdersStatus currentStatus = order.getOrdersStatus();

        if (!isValidTransition(currentStatus, newStatus)) {
            throw new IllegalArgumentException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }
        // Create status history entry
        OrderStatusHistory orderStatusHistory = new OrderStatusHistory();
        orderStatusHistory.setStatus(String.valueOf(newStatus));
        orderStatusHistory.setChangedAt(LocalDateTime.now());
        orderStatusHistory.setChangedBy(adminUsername);
        orderStatusHistory.setOrder(order); // assuming a relationship exists
        // Update order
        order.setOrdersStatus(newStatus);
//        order.setStatus(OrdersStatus.valueOf(newStatus.name()));
        order.getOrderStatusHistoryList().add(orderStatusHistory);
        return orderRepository.save(order);
    }

    private boolean isValidTransition(OrdersStatus current, OrdersStatus next) {
        if (current == null) {
            return next == OrdersStatus.PENDING; // Only allow starting from PENDING
        }
        return switch (current) {
            case PENDING -> next == OrdersStatus.PROCESSED;
            case PROCESSED -> next == OrdersStatus.SHIPPED;
            case SHIPPED -> next == OrdersStatus.DELIVERED;
            default -> false;
        };
    }

























    public List<Map<String, Object>> getDailyTotals(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        return orderRepository.getDailyTotalsInMonth(
                startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
    }

    public List<WeeklyRevenueSummary> getWeeklyTotals(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        return orderRepository.getWeeklyTotalsInMonth(
                startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
    }




    public List<MonthlyRevenueSummary> getMonthlyRevenue(int year) {
        List<Object[]> results = orderRepository.getMonthlyRevenue(year);
        List<MonthlyRevenueSummary> summary = new ArrayList<>();

        for (Object[] row : results) {
            int month = (int) row[0];
            double total = (double) row[1];
            summary.add(new MonthlyRevenueSummary(month, total));
        }
        return summary;
    }





    public List<MonthlyOrderSummary> getMonthlyOrders(int year) {
        return orderRepository.findMonthlyOrders(year);
    }

















    public List<Order> getOrdersByFarmer(Long farmerId) {
        return orderRepository.findByFarmerId(farmerId);
    }



    public Page<Order> getOrdersByFarmer(Long farmerId, Pageable pageable) {
        return orderRepository.findByFarmerId(farmerId, pageable);
    }

    public OrderDto getOrderDetails(Long orderId, Long farmerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // Verify that at least one item in the order belongs to this farmer
        boolean belongsToFarmer = order.getOrderDetails().stream()
                .anyMatch(item -> item.getProduct().getFarmer().getId().equals(farmerId));

        if (!belongsToFarmer) {
            throw new UnauthorizedAccessException("You don't have permission to view this order");
        }

        return convertToDTO(order, farmerId);
    }

    public OrderDto convertToDTO(Order order, Long farmerId) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setCustomerName(order.getCustomer().getFullName());
        dto.setCustomerId(Long.valueOf(order.getCustomer().getId()));
        dto.setCustomerEmail(order.getCustomer().getUsername());
        //dto.setCustomerEmail(order.getDeliveryInfo().);
        //dto.setStatus(order.getStatus());
        dto.setStatus(order.getOrdersStatus());
        // dto.setTotalAmount(order.getTotalAmount());
        dto.setTotalAmount(BigDecimal.valueOf(order.getAmount()));

        DeliveryDetailsDTO deliveryDTO = new DeliveryDetailsDTO();
        deliveryDTO.setDeliveryNote(order.getDeliveryInfo().getNotes());
        deliveryDTO.setArea(order.getDeliveryInfo().getArea());
        deliveryDTO.setRecipient(order.getDeliveryInfo().getRecipientName());
        deliveryDTO.setPhone(order.getDeliveryInfo().getPhoneNumber());
        deliveryDTO.setGpsAddress(order.getDeliveryInfo().getDigitalAddress());
        deliveryDTO.setMajorLandmark(order.getDeliveryInfo().getLandmark());
        deliveryDTO.setRegion(order.getDeliveryInfo().getRegion());
        deliveryDTO.setStreet(order.getDeliveryInfo().getStreet());
        deliveryDTO.setDistrict(order.getDeliveryInfo().getDistrict());
        dto.setDeliveryDetails(deliveryDTO);

        OrderSummaryDTO summary = new OrderSummaryDTO();
        summary.setTotalAmount(BigDecimal.valueOf(order.getAmount()));
        //summary.setTotalAmount(order.getTotalAmount());
        summary.setPaymentMethod(order.getPaymentMethod() != null ?
                order.getPaymentMethod().toString() : "Not specified");
        summary.setPaymentStatus(order.isPaid() ? "Paid" : "Unpaid");
        dto.setSummary(summary);


        // Only include items that belong to this farmer
        List<OrderItemDto> items = order.getOrderDetails().stream()
                .peek(item -> {
                    System.out.println("Product: " + item.getProduct().getProductName());
                    User farmer = item.getProduct().getFarmer();
                    System.out.println("Product Farmer ID: " + (farmer != null ? farmer.getId() : "null"));
                    System.out.println("Current Farmer ID: " + farmerId);
                })
                .filter(item -> {
                    Long productFarmerId = Long.valueOf(item.getProduct().getFarmer().getId());
                    System.out.println("Comparing farmer IDs: " + productFarmerId + " (" + productFarmerId.getClass().getName() + ") vs " + farmerId + " (" + farmerId.getClass().getName() + ")");
                    boolean equals = productFarmerId.equals(farmerId);
                    System.out.println("equals result: " + equals);
                    return equals;
                })
                .map(this::convertItemToDTO)
                .collect(Collectors.toList());

        System.out.println(STR."Order ID: \{order.getId()} has \{items.size()} items.");

        dto.setItems(items);
        return dto;

    }

    public OrderItemDto convertItemToDTO(OrderDetails item) {
        OrderItemDto dto = new OrderItemDto();
        dto.setProductName(item.getProduct().getProductName());
        dto.setOrderedItemStatus(item.getOrderedItemStatus());
        dto.setQuantity(item.getQuantity());
        dto.setId(item.getProduct().getId());

        dto.setPrice(BigDecimal.valueOf(item.getPrice()));
        BigDecimal price = BigDecimal.valueOf(item.getPrice());
        dto.setPrice(BigDecimal.valueOf(item.getPrice()));
        BigDecimal total = price.multiply(BigDecimal.valueOf(item.getQuantity()));
        dto.setTotal(total);
        return dto;
    }




















    // GET ORDERS BY FARMER


    public List<Order> getOrdersByCurrentFarmer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return orderRepository.findByFarmerId(Long.valueOf(currentUser.getId()));
    }

    public long getOrderCountByCurrentFarmer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return orderRepository.countByFarmerId(Long.valueOf(currentUser.getId()));
    }

    public BigDecimal getTotalSalesByCurrentFarmer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return orderRepository.getTotalSalesByFarmerId(Long.valueOf(currentUser.getId()));
    }


    //Get monthly order summary for a specific farmer
    public List<MonthlyOrderSummary> getMonthlyOrdersForFarmer(int year, Long farmerId) {
        return orderRepository.findMonthlyOrdersByFarmer(year, farmerId);
    }

    public List<MonthlyRevenueSummary> getMonthlyRevenueForFarmer(int year, Long farmerId) {
        return orderRepository.getMonthlyRevenueByFarmer(year, farmerId);
    }


    public List<WeeklyRevenueSummary> getWeeklyTotalsForFarmer(int year, int month, Long farmerId) {
        return orderRepository.getWeeklyTotalsInMonthByFarmer(year, month, farmerId);
    }



    // Daily revenue for a specific farmer in a given month
    public List<Map<String, Object>> getDailyRevenueForFarmer(int year, int month, Long farmerId) {
        return orderRepository.getDailyTotalsInMonthByFarmer(year, month, farmerId);
    }

}
