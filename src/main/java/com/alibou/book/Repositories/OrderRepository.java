package com.alibou.book.Repositories;

import com.alibou.book.Entity.Order;
import com.alibou.book.Entity.OrderDetailStatus;
import com.alibou.book.Entity.OrderDetails;
import com.alibou.book.Entity.ReturnRequest;
import com.alibou.book.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomer(User customer);


        Optional<Order> findByIdAndCustomer(Long id, User customer);
    public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {
        Optional<OrderDetails> findByOrderIdAndProductId(Long orderId, Long productId);

        @Modifying
        @Query("UPDATE OrderDetails od SET od.status = :status WHERE od.order.id = :orderId AND od.product.id = :productId")
        void updateStatusForOrderProduct(@Param("orderId") Long orderId,
                                         @Param("productId") Long productId,
                                         @Param("status") OrderDetailStatus status);
    }

    List<Order>findByCustomer_Id(Long userId);

    long count();
    // In OrderRepository
    long countByStatus(String status);  // Count by order status
    long countByCustomerId(Long customerId);  // Count by customer



    @Query("SELECT SUM(o.amount) FROM Order o")
    BigDecimal getTotalAmount();

    // For cases where some orders might have null amounts
    @Query("SELECT COALESCE(SUM(o.amount), 0) FROM Order o")
    BigDecimal getTotalAmountSafe();


}
