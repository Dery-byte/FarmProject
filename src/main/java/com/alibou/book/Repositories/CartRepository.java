package com.alibou.book.Repositories;

import com.alibou.book.Entity.Cart;
import com.alibou.book.Entity.CartStatus;
import com.alibou.book.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserAndStatus(User user, CartStatus status);
}