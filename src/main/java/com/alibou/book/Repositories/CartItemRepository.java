package com.alibou.book.Repositories;

import com.alibou.book.Entity.Cart;
import com.alibou.book.Entity.CartItem;
import com.alibou.book.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
        void deleteByCartAndProduct(Cart cart, Product product);
    }

