package com.alibou.book.Services;


import com.alibou.book.Entity.Cart;
import com.alibou.book.Entity.CartItem;
import com.alibou.book.Entity.CartStatus;
import com.alibou.book.Entity.Product;
import com.alibou.book.Repositories.CartItemRepository;
import com.alibou.book.Repositories.CartRepository;
import com.alibou.book.Repositories.ProductRepository;
import com.alibou.book.user.User;
import com.alibou.book.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;


@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private final UserDetailsService userDetailsService;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository, UserRepository userRepository, UserDetailsService userDetailsService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
    }


    public Cart addToCart(Long productId, int quantity, Principal principal) {
        Cart cart = getUserCart(principal);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if product already in cart
        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            // Update existing item
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setSubtotal(existingItem.getQuantity() * existingItem.getPrice());
        } else {
            // Add new item
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setPrice(product.getPrice());
            item.setSubtotal(quantity * product.getPrice());

            cart.getItems().add(item);
        }

        return cartRepository.save(cart);
    }


    public Cart getUserCart(Principal principal) {
//        String email = principal.getName();
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("User not found"));

        return cartRepository.findByUserAndStatus(user, CartStatus.OPEN)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setStatus(CartStatus.OPEN);
                    return cartRepository.save(newCart);
                });
    }



//    DELETE CART ITEM AND Product
@Transactional
public boolean removeFromCart(Long productId, Principal principal) {
    Cart cart = getUserCart(principal);
    Optional<CartItem> itemToRemove = cart.getItems().stream()
            .filter(item -> item.getProduct().getId().equals(productId))
            .findFirst();
    if (itemToRemove.isEmpty()) {
        return false; // Product not in cart
    }
    // Remove item from cart's item list (will trigger delete if orphanRemoval = true)
    cart.getItems().remove(itemToRemove.get());

    return true;
}








//    DISPLAY CART TOTAL

    public double getCartTotal(Cart cart) {
        return cart.getItems().stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
    }


}
