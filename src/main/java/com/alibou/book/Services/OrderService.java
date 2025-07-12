package com.alibou.book.Services;
import com.alibou.book.DTO.*;
import com.alibou.book.Entity.*;
import com.alibou.book.Repositories.*;
import com.alibou.book.Repositories.PaymentStatusRepository;
import com.alibou.book.Repositories.Projections.WeeklyRevenueSummary;
import com.alibou.book.exception.InsufficientStockException;
import com.alibou.book.exception.ResourceNotFoundException;
import com.alibou.book.exception.UnauthorizedAccessException;
import com.alibou.book.user.User;
import com.cloudinary.utils.StringUtils;
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
import java.util.Optional;
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
    private final OrderDetailsRepository orderDetailsRepository;

    private final PaymentStatusRepository paymentStatusRepository;
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


//    @Transactional
//    public Order placeOrder(Principal principal, PlaceOrderRequest request) {
//        // 1. Get user cart
//        Cart cart = cartService.getUserCart(principal);
//        if (cart.getItems().isEmpty()) {
//            throw new IllegalStateException("Cannot place order: Cart is empty");
//        }
//        // 2. Validate and create delivery info
//        Delivery deliveryInfo = new Delivery(
//                request.getDelivery().getRecipientName(),
//                request.getDelivery().getPhoneNumber(),
//                request.getDelivery().getDigitalAddress(),
//                request.getDelivery().getArea(),
//                request.getDelivery().getDistrict(),
//                request.getDelivery().getNotes(),
//                request.getDelivery().getLandmark(),
//                request.getDelivery().getStreet(),
//                request.getDelivery().getRegion()
//        );
//        deliveryInfo.validate(); // Throws IllegalArgumentException if invalid
//
//
//        // Create Payment during Order Processing
//
//
//
//        // 3. Create order with delivery info
//        Order order = new Order();
//        order.setOrderDate(LocalDateTime.now());
//        order.setCustomer(cart.getUser());
//        order.setAmount(cartService.getCartTotal(cart));
//        order.setOrdersStatus(OrdersStatus.PENDING);
//        order.setStatus(OrderStatus.PENDING);
//        order.setPaid(false);
//        order.setDeliveryInfo(deliveryInfo); // Embedded delivery details
//
//
//        Payment payment = new Payment();
//        payment.setPaymentDate(LocalDateTime.now());
//        payment.setAmount(cartService.getCartTotal(cart));
//        payment.setPaymentMethod(PaymentMethod.MOBILE_MONEY);
//        payment.setStatus(PaymentStatus.PENDING);
//        payment.setTransactionId("999987363536839");
//        payment.setOrder(order);
//        paymentRepository.save(payment);
//
//        // 4. Convert cart items to order details
//        List<OrderDetails> orderDetailsList = cart.getItems().stream()
//                .map(item -> {
//                    OrderDetails detail = new OrderDetails();
//                    detail.setOrder(order);
//                    detail.setProduct(item.getProduct());
//                    detail.setQuantity(item.getQuantity());
//                    detail.setPrice(item.getPrice());
//
//                    // Update product stock
//                    Product product = item.getProduct();
//                    product.setQuantity(product.getQuantity() - item.getQuantity());
//                    productRepository.save(product);
//
//                    return detail;
//                })
//                .toList();
//
//        order.setOrderDetails(orderDetailsList);
//
//        // 5. Save and clear cart
//        Order savedOrder = orderRepository.save(order);
//        cart.getItems().clear();
//        return savedOrder;
//    }
//


    @Transactional
    public Order placeOrder(Principal principal, String externalRef) {
        // 1. Get user cart
        Cart cart = cartService.getUserCart(principal);
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot place order: Cart is empty");
        }

        // 2. Validate stock for all cart items before processing
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            if (product.getQuantity() < item.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getProductName());
            }
        }

        // 3. Validate and create delivery info
//        Delivery deliveryInfo = new Delivery(
//                request.getDelivery().getRecipientName(),
//                request.getDelivery().getPhoneNumber(),
//                request.getDelivery().getDigitalAddress(),
//                request.getDelivery().getArea(),
//                request.getDelivery().getDistrict(),
//                request.getDelivery().getNotes(),
//                request.getDelivery().getLandmark(),
//                request.getDelivery().getStreet(),
//                request.getDelivery().getRegion()
//        );
       // deliveryInfo.validate(); // Throws IllegalArgumentException if invalid

        // 4. Create order with delivery info
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setCustomer(cart.getUser());
        order.setAmount(cartService.getCartTotal(cart));
        order.setOrdersStatus(OrdersStatus.PENDING);
        order.setStatus(OrderStatus.PENDING);
        order.setPaid(false);
        order.setExternalRef(externalRef);
      //  order.setDeliveryInfo(deliveryInfo);
       // order.setExternalRef(request.getDelivery().getExternalRef());
        // 5. Create and attach payment
//        PaymentStatuss payment = new PaymentStatuss();
//        //payment.setTimestamp(String.valueOf(LocalDateTime.now()));
//       // payment.setPaymentDate(LocalDateTime.now());
//        payment.setAmount(cartService.getCartTotal(cart));
//       // payment.setPaymentMethod(PaymentMethod.MOBILE_MONEY);
//        payment.setExternalRef(externalRef);
//        //payment.setStatus(PaymentStatus.PENDING);
//        payment.setTransactionId(Long.parseLong("999987363536839"));
//       // payment.
//        //payment.setOrder(order);
//        paymentStatusRepository.save(payment);

        // 6. Convert cart items to order details and deduct stock
        List<OrderDetails> orderDetailsList = cart.getItems().stream()
                .map(item -> {
                    Product product = item.getProduct();
                    // Deduct stock now that validation is passed
                   // product.setQuantity(product.getQuantity() - item.getQuantity());
                    productRepository.save(product);

                    OrderDetails detail = new OrderDetails();
                    detail.setOrder(order);
                    detail.setProduct(product);
                    detail.setQuantity(item.getQuantity());
                    detail.setPrice(item.getPrice());
                    return detail;
                })
                .toList();

        order.setOrderDetails(orderDetailsList);

        // 7. Save order and clear cart
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

    public Optional<Order> findByExternalReference(String externalReference) {
        return orderRepository.findByExternalRef(externalReference);
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

//    private boolean isValidTransition(OrdersStatus current, OrdersStatus next) {
//        if (current == null) {
//            return next == OrdersStatus.PENDING; // Only allow starting from PENDING
//        }
//        return switch (current) {
//            case PENDING -> next == OrdersStatus.PROCESSED;
//            case PROCESSED -> next == OrdersStatus.SHIPPED;
//            case SHIPPED -> next == OrdersStatus.DELIVERED;
//            default -> false;
//        };
//    }

























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
        dto.setOrderId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setCustomerName(order.getCustomer().getFullName());
        dto.setCustomerId(Long.valueOf(order.getCustomer().getId()));
        dto.setCustomerEmail(order.getCustomer().getUsername());
        //dto.setCustomerEmail(order.getDeliveryInfo().);
        //dto.setStatus(order.getStatus());
        dto.setStatus(order.getOrdersStatus());
        // dto.setTotalAmount(order.getTotalAmount());
        dto.setTotalAmount(BigDecimal.valueOf(order.getAmount()));

        DeliveryDTO deliveryDTO = new DeliveryDTO();

        deliveryDTO.setNotes(order.getCustomer().getDelivery().getNotes());
        deliveryDTO.setArea(order.getCustomer().getDelivery().getArea());
        deliveryDTO.setRecipientName(order.getCustomer().getDelivery().getRecipientName());
        deliveryDTO.setPhoneNumber(order.getCustomer().getDelivery().getPhoneNumber());
        deliveryDTO.setDigitalAddress(order.getCustomer().getDelivery().getDigitalAddress());
        deliveryDTO.setLandmark(order.getCustomer().getDelivery().getLandmark());
        deliveryDTO.setRegion(order.getCustomer().getDelivery().getRegion());
        deliveryDTO.setStreet(order.getCustomer().getDelivery().getStreet());
        deliveryDTO.setDistrict(order.getCustomer().getDelivery().getDistrict());
        dto.setDelivery(deliveryDTO);
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
        dto.setOrderDetailsId(item.getId());
        dto.setProductName(item.getProduct().getProductName());
        dto.setOrderedItemStatus(item.getOrderedItemStatus());
        dto.setQuantity(item.getQuantity());
        dto.setProductId(item.getProduct().getId());
        dto.setPrice(BigDecimal.valueOf(item.getPrice()));
        BigDecimal price = BigDecimal.valueOf(item.getPrice());
        dto.setPrice(BigDecimal.valueOf(item.getPrice()));
        BigDecimal total = price.multiply(BigDecimal.valueOf(item.getQuantity()));
        dto.setTotal(total);
        dto.setOrderedItemStatusHistoryList(item.getOrderedItemStatusHistoryList());

        return dto;
    }











































    // GET ORDERS BY FARMER


    public List<Order> getOrdersByCurrentFarmer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return orderRepository.findByFarmerId(Long.valueOf(currentUser.getId()));
    }

    public long getOrderCountByCurrentFarmer(Long farmerId) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        User currentUser = (User) authentication.getPrincipal();
        return orderRepository.countByFarmerId(farmerId);
    }

    public BigDecimal getTotalSalesByCurrentFarmer(Long farmerId) {

        return orderRepository.getTotalSalesByFarmerId(farmerId);
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













// validation for the item
private boolean isValidTransitionItemStatusOrdered(OrderedItemStatus current, OrderedItemStatus next) {
    if (next == null) {
        return false; // Never allow transition to null
    }
    if (current == null) {
        return next == OrderedItemStatus.PENDING; // Only allow starting from PENDING
    }
    return switch (current) {
        case PENDING -> next == OrderedItemStatus.PROCESSED;
        case PROCESSED -> next == OrderedItemStatus.SHIPPED;
        case SHIPPED -> next == OrderedItemStatus.DELIVERED;
        default -> false;
    };
}




    @Transactional
    public OrderDetails updateOrderedItemStatus(Long ordererdItemId, OrderedItemStatus newStatus, String adminUsername) {
        OrderDetails orderDetails = orderDetailsRepository.findById(ordererdItemId).
                orElseThrow(()-> new  RuntimeException("Order Item Not found"));
        //OrderedItemStatus currentOrderedItemStatus = orderDetails.getOrderedItemStatus();

       System.out.println(orderDetails.getId());
        System.out.println(orderDetails.getOrder().getId());

        System.out.println(newStatus);
        OrderedItemStatus currentOrderedItemStatus = orderDetails.getOrderedItemStatus();


        if (!isValidTransitionItemStatusOrdered(currentOrderedItemStatus, newStatus)) {
            throw new IllegalArgumentException("Invalid status transition from " + currentOrderedItemStatus + " to " + newStatus);
        }
        // Create status history entry
OrderedItemStatusHistory orderedItemStatusHistory = new  OrderedItemStatusHistory();
        orderedItemStatusHistory.setStatus(String.valueOf(newStatus));
        orderedItemStatusHistory.setChangedAt(LocalDateTime.now());
        orderedItemStatusHistory.setChangedBy(adminUsername);
        orderedItemStatusHistory.setOrderDetails(orderDetails); // assuming a relationship exists
        // Update order
        //orderDetails.setStatus(newStatus);
        orderDetails.setOrderedItemStatus(newStatus);
        orderDetails.setStatus(OrderDetailStatus.valueOf(newStatus.toString()));
        //order.setOrdersStatus(newStatus);
//        order.setStatus(OrdersStatus.valueOf(newStatus.name()));
        orderDetails.getOrderedItemStatusHistoryList().add(orderedItemStatusHistory);


        Order order = orderDetails.getOrder();


        updateParentOrderStatus(order);
        OrdersStatus newOrderStatus = mapItemStatusToOrderStatus(newStatus);
        if (!newOrderStatus.equals(order.getOrdersStatus())) {
            OrderStatusHistory orderHistory = new OrderStatusHistory();
            orderHistory.setStatus(newOrderStatus.toString());
            orderHistory.setChangedAt(LocalDateTime.now());
            orderHistory.setChangedBy(adminUsername);
            orderHistory.setOrder(order);
            order.setOrdersStatus(newOrderStatus);
            order.getOrderStatusHistoryList().add(orderHistory);
            orderRepository.save(order);
        }


        //order.getOrderStatusHistoryList().add(orderStatusHistory);
        return orderDetailsRepository.save(orderDetails);
    }

    private void updateParentOrderStatus(Order order) {
        // Get the most "advanced" status from all items
        OrderedItemStatus highestItemStatus = order.getOrderDetails().stream()
                .map(OrderDetails::getOrderedItemStatus)
                .max(Enum::compareTo)
                .orElse(OrderedItemStatus.PENDING);

        // Map the item status to order status
        OrdersStatus newOrderStatus = mapItemStatusToOrderStatus(highestItemStatus);

        // Only update if different to avoid unnecessary updates
        if (!newOrderStatus.equals(order.getOrdersStatus())) {
            order.setOrdersStatus(newOrderStatus);
            orderRepository.save(order);
        }
    }


    private OrdersStatus mapItemStatusToOrderStatus(OrderedItemStatus itemStatus) {
        // Map between the item status enum and order status enum
        return switch (itemStatus) {
            case PENDING -> OrdersStatus.PENDING;
            case PROCESSED -> OrdersStatus.PROCESSED;
            case SHIPPED -> OrdersStatus.SHIPPED;
            case DELIVERED -> OrdersStatus.DELIVERED;
            default -> OrdersStatus.PENDING;
        };
    }










































    // New Methods


    @Transactional
    public Order placeOrderAfterPayment(Principal principal, String transactionId) {
        // 1. Get user cart
        Cart cart = cartService.getUserCart(principal);
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot place order: Cart is empty");
        }

        // 2. Validate stock
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            if (product.getQuantity() < item.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getProductName());
            }
        }

        // 3. Create basic order (without delivery info)
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setCustomer(cart.getUser());
        order.setAmount(cartService.getCartTotal(cart));
        //order.setOrdersStatus(OrdersStatus.PROCESSING);
        //order.setStatus(OrderStatus.PROCESSING);
        order.setPaid(true); // Since payment is confirmed
       // order.setDeliveryInfo(deliveryInfo);


        // 4. Create payment record
        Payment payment = new Payment();
        payment.setPaymentDate(LocalDateTime.now());
        payment.setAmount(cartService.getCartTotal(cart));
        payment.setPaymentMethod(PaymentMethod.MOBILE_MONEY);
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setTransactionId(transactionId);
        payment.setOrder(order);
        paymentRepository.save(payment);

        // 5. Convert cart items to order details and deduct stock
        List<OrderDetails> orderDetailsList = cart.getItems().stream()
                .map(item -> {
                    Product product = item.getProduct();
                    product.setQuantity(product.getQuantity() - item.getQuantity());
                    productRepository.save(product);

                    OrderDetails detail = new OrderDetails();
                    detail.setOrder(order);
                    detail.setProduct(product);
                    detail.setQuantity(item.getQuantity());
                    detail.setPrice(item.getPrice());
                    return detail;
                })
                .toList();

        order.setOrderDetails(orderDetailsList);
        Order savedOrder = orderRepository.save(order);

        // 6. Clear cart only after successful order placement
        cart.getItems().clear();
        cartRepository.save(cart);

        return savedOrder;
    }




































    @Transactional
    public Order verifyAndUpdateOrder(Principal principal, Long orderId, String newExternalRef) {
        // Validate inputs
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        if (StringUtils.isBlank(newExternalRef)) {
            throw new IllegalArgumentException("External reference cannot be blank");
        }

        // Get authenticated user
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());

        // Find order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with ID: " + orderId));

        // Verify ownership
        if (!order.getCustomer().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("Unauthorized order access");
        }

        // Store the old externalRef for reference if needed
        String oldExternalRef = order.getExternalRef();
//        log.info("Updating externalRef for order {} from {} to {}",
//                orderId, oldExternalRef, newExternalRef);

        // Update with new externalRef
        order.setExternalRef(newExternalRef);
       // order.setLastModifiedDate(LocalDateTime.now());

        return orderRepository.save(order);
    }


}
