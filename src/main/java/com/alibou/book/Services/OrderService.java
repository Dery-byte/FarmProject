package com.alibou.book.Services;
import com.alibou.book.DTO.OrderItemRequest;
import com.alibou.book.DTO.PlaceOrderRequest;
import com.alibou.book.Entity.Order;
import com.alibou.book.Entity.OrderDetails;
import com.alibou.book.Entity.Product;
import com.alibou.book.Repositories.OrderRepository;
import com.alibou.book.Repositories.ProductRepository;
import com.alibou.book.security.UserDetailsServiceImpl;
import com.alibou.book.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

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
//    private final UserRepository userRepository;

    public Order placeOrder(PlaceOrderRequest request, Principal principal) {
        // Get logged-in user by username (email)

        // Authentication check
        if (principal == null) {
            throw new IllegalArgumentException("User must be authenticated to add a product.");
        }
        // Load user and validate farm
        User customer = (User) userDetailsService.loadUserByUsername(principal.getName());



//        String username = principal.getName();
//        User customer = userRepository.findByEmail(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setPaid(false);
        order.setStatus(PENDING);

        List<OrderDetails> orderDetailsList = new ArrayList<>();
        double totalAmount = 0.0;

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            OrderDetails details = new OrderDetails();
            details.setProduct(product);
            details.setQuantity(itemRequest.getQuantity());
            details.setPrice(product.getPrice());
            details.setOrder(order);

            totalAmount += product.getPrice() * itemRequest.getQuantity();
            orderDetailsList.add(details);
        }

        order.setAmount(totalAmount);
        order.setOrderDetails(orderDetailsList);

        return orderRepository.save(order); // Cascade saves OrderDetails too
    }
}
