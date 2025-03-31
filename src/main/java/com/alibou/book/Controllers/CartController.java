package com.alibou.book.Controllers;

import com.alibou.book.DTO.AddToCartRequest;
import com.alibou.book.Entity.Cart;
import com.alibou.book.Services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(@RequestBody AddToCartRequest request, Principal principal) {
        Cart updatedCart = cartService.addToCart(request.getProductId(), request.getQuantity(), principal);
        return ResponseEntity.ok(updatedCart);
    }


    @GetMapping("/list")
    public ResponseEntity<Cart> getCart(Principal principal) {
        Cart cart = cartService.getUserCart(principal);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Map<String, Object>> removeProduct(@PathVariable Long productId, Principal principal) {
        boolean removed = cartService.removeFromCart(productId, principal);
        Map<String, Object> response = new HashMap<>();
        if (removed) {
            response.put("success", true);
            response.put("message", "Product with ID: " + productId + " removed from cart");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Product with ID: " + productId + " not found in cart");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


}
