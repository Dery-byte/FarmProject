package com.alibou.book.Repositories;

import com.alibou.book.Entity.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {

    Optional<OrderDetails> findByOrderIdAndProductId(Long orderId, Long productId);

    List<OrderDetails> findByProductId(Long productId);

}
