package com.alibou.book.Services;
import com.alibou.book.DTO.DeliveryInfoRequest;
import com.alibou.book.DTO.PlaceOrderRequest;
import com.alibou.book.Entity.*;
import com.alibou.book.Repositories.CartRepository;
import com.alibou.book.Repositories.OrderRepository;
import com.alibou.book.Repositories.ProductRepository;
import com.alibou.book.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.context.DelegatingApplicationListener;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.alibou.book.Entity.OrderStatus.PENDING;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    private final UserDetailsService userDetailsService;
    private final CartService cartService;
    private final CartRepository cartRepository;
    private final DelegatingApplicationListener delegatingApplicationListener;
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

        // 3. Create order with delivery info
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setCustomer(cart.getUser());
        order.setAmount(cartService.getCartTotal(cart));
        order.setOrdersStatus(OrdersStatus.PENDING);
        order.setStatus(OrderStatus.PENDING);
        order.setPaid(false);
        order.setDeliveryInfo(deliveryInfo); // Embedded delivery details

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


}
