package com.alibou.book.Services;
import com.alibou.book.DTO.OrderItemRequest;
import com.alibou.book.DTO.PlaceOrderRequest;
import com.alibou.book.Entity.*;
import com.alibou.book.Repositories.CartRepository;
import com.alibou.book.Repositories.OrderRepository;
import com.alibou.book.Repositories.ProductRepository;
import com.alibou.book.security.UserDetailsServiceImpl;
import com.alibou.book.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
//    private final UserRepository userRepository;
@Transactional
public Order placeOrder(Principal principal) {
    Cart cart = cartService.getUserCart(principal);

    if (cart.getItems().isEmpty()) {
        throw new RuntimeException("Cart is empty");
    }

    Order order = new Order();
    order.setOrderDate(LocalDateTime.now());
    order.setCustomer(cart.getUser());
    order.setAmount(cartService.getCartTotal(cart));
    order.setStatus(PENDING);
    order.setPaid(false); // until payment is made

    List<OrderDetails> orderDetailsList = new ArrayList<>();

    for (CartItem item : cart.getItems()) {
        OrderDetails detail = new OrderDetails();
        detail.setOrder(order);
        detail.setProduct(item.getProduct());
        detail.setQuantity(item.getQuantity());
        detail.setPrice(item.getPrice());
        orderDetailsList.add(detail);


        // Update product quantity
        Product product = item.getProduct();
        product.setQuantity(product.getQuantity() - item.getQuantity());
        productRepository.save(product);
    }

    order.setOrderDetails(orderDetailsList);
    orderRepository.save(order);

    // Clear the cart
    cart.getItems().clear();
    cartRepository.save(cart);

    return order;
}

}
