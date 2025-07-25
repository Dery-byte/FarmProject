package com.alibou.book.Repositories;

import com.alibou.book.DTO.MonthlyOrderSummary;
import com.alibou.book.DTO.MonthlyRevenueSummary;
import com.alibou.book.Entity.Order;
import com.alibou.book.Entity.OrderDetailStatus;
import com.alibou.book.Entity.OrderDetails;
import com.alibou.book.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.alibou.book.Repositories.Projections.WeeklyRevenueSummary;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomer(User customer);


        Optional<Order> findByIdAndCustomer(Long id, User customer);

    Optional<Order> findByExternalRef(String externalref);

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
























    // Daily totals
    @Query("SELECT DATE(o.orderDate) AS date, SUM(o.amount) AS totalAmount " +
            "FROM Order o " +
            "WHERE o.orderDate BETWEEN :start AND :end " +
            "AND o.isPaid = false " +
            "GROUP BY DATE(o.orderDate) " +
            "ORDER BY DATE(o.orderDate)")
    List<Map<String, Object>> getDailyTotalsInMonth(@Param("start") LocalDateTime start,
                                                    @Param("end") LocalDateTime end);

    // Weekly totals
    @Query("SELECT FUNCTION('WEEK', o.orderDate) AS week, SUM(o.amount) AS totalAmount " +
            "FROM Order o " +
            "WHERE o.orderDate BETWEEN :start AND :end " +
            "AND o.isPaid = false " +
            "GROUP BY FUNCTION('WEEK', o.orderDate) " +
            "ORDER BY FUNCTION('WEEK', o.orderDate)")
    List<WeeklyRevenueSummary> getWeeklyTotalsInMonth(@Param("start") LocalDateTime start,
                                                      @Param("end") LocalDateTime end);



    @Query("SELECT FUNCTION('MONTH', o.orderDate) AS month, SUM(o.amount) AS totalAmount " +
            "FROM Order o " +
            "WHERE FUNCTION('YEAR', o.orderDate) = :year AND o.isPaid = false " +
            "GROUP BY FUNCTION('MONTH', o.orderDate) " +
            "ORDER BY month")
    List<Object[]> getMonthlyRevenue(@Param("year") int year);






    @Query("SELECT new com.alibou.book.DTO.MonthlyOrderSummary(FUNCTION('MONTH', o.orderDate), COUNT(o)) " +
            "FROM Order o " +
            "WHERE FUNCTION('YEAR', o.orderDate) = :year " +
            "GROUP BY FUNCTION('MONTH', o.orderDate) " +
            "ORDER BY FUNCTION('MONTH', o.orderDate)")
    List<MonthlyOrderSummary> findMonthlyOrders(@Param("year") int year);












    //ORDERS BY FARMERS

    // Correct method name based on your actual entity relationships
    //@Query("SELECT DISTINCT o FROM Order o JOIN o.orderDetails od WHERE od.product.farmer.id = :farmerId")

    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN FETCH o.orderDetails od " +
            "JOIN od.product p " +
            "WHERE p.farmer.id = :farmerId")
    List<Order> findByFarmerId(@Param("farmerId") Long farmerId);

    // OR using derived query method (choose one approach)
    List<Order> findByOrderDetailsProductFarmerId(Long farmerId);

    // For pagination
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderDetails od WHERE od.product.farmer.id = :farmerId")
    Page<Order> findByFarmerId(@Param("farmerId") Long farmerId, Pageable pageable);

















    //ORDER COUNT BY FARMER



    // Count orders for a specific farmer
    @Query("SELECT COUNT(DISTINCT o) FROM Order o JOIN o.orderDetails od JOIN od.product p WHERE p.farmer.id = :farmerId AND o.isPaid = false")
    Long countByFarmerId(Long farmerId);

    // Calculate total sales for a specific farmer
    @Query("SELECT COALESCE(SUM(od.price * od.quantity), 0) FROM Order o JOIN o.orderDetails od JOIN od.product p WHERE p.farmer.id = :farmerId AND o.isPaid = false")
    BigDecimal getTotalSalesByFarmerId(Long farmerId);














    @Query("""
    SELECT new com.alibou.book.DTO.MonthlyOrderSummary(
        FUNCTION('MONTH', o.orderDate), COUNT(DISTINCT o.id))
    FROM Order o
    JOIN o.orderDetails od
    JOIN od.product p
    WHERE FUNCTION('YEAR', o.orderDate) = :year
      AND p.farmer.id = :farmerId
    GROUP BY FUNCTION('MONTH', o.orderDate)
    ORDER BY FUNCTION('MONTH', o.orderDate)
""")
    List<MonthlyOrderSummary> findMonthlyOrdersByFarmer(@Param("year") int year, @Param("farmerId") Long farmerId);






    @Query("""
    SELECT new com.alibou.book.DTO.MonthlyRevenueSummary(FUNCTION('MONTH', o.orderDate), SUM(o.amount))
    FROM Order o
    WHERE FUNCTION('YEAR', o.orderDate) = :year
      AND o.isPaid = false
      AND EXISTS (
          SELECT 1 FROM OrderDetails od
          WHERE od.order = o AND od.product.farmer.id = :farmerId
      )
    GROUP BY FUNCTION('MONTH', o.orderDate)
    ORDER BY FUNCTION('MONTH', o.orderDate)
    """)
    List<MonthlyRevenueSummary> getMonthlyRevenueByFarmer(@Param("year") int year, @Param("farmerId") Long farmerId);








    @Query("""
    SELECT new com.alibou.book.DTO.WeeklyRevenueSummary(FUNCTION('WEEK', o.orderDate), SUM(o.amount))
    FROM Order o
    WHERE FUNCTION('YEAR', o.orderDate) = :year
      AND FUNCTION('MONTH', o.orderDate) = :month
      AND o.isPaid = false
      AND EXISTS (
          SELECT 1 FROM OrderDetails od
          WHERE od.order = o AND od.product.farmer.id = :farmerId
      )
    GROUP BY FUNCTION('WEEK', o.orderDate)
    ORDER BY FUNCTION('WEEK', o.orderDate)
    """)
    List<WeeklyRevenueSummary> getWeeklyTotalsInMonthByFarmer(
            @Param("year") int year,
            @Param("month") int month,
            @Param("farmerId") Long farmerId);










    // Daily totals
    @Query("""
    SELECT DATE(o.orderDate) AS date, SUM(o.amount) AS totalAmount
    FROM Order o
    WHERE FUNCTION('YEAR', o.orderDate) = :year
      AND FUNCTION('MONTH', o.orderDate) = :month
      AND o.isPaid = false
      AND EXISTS (
          SELECT 1 FROM OrderDetails od
          WHERE od.order = o AND od.product.farmer.id = :farmerId
      )
    GROUP BY DATE(o.orderDate)
    ORDER BY DATE(o.orderDate)
    """)
    List<Map<String, Object>> getDailyTotalsInMonthByFarmer(
            @Param("year") int year,
            @Param("month") int month,
            @Param("farmerId") Long farmerId);


}
