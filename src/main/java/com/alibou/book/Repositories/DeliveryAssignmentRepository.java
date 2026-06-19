package com.alibou.book.Repositories;

import com.alibou.book.Entity.DeliveryAssignment;
import com.alibou.book.Entity.DeliveryAssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryAssignmentRepository extends JpaRepository<DeliveryAssignment, Long> {

    // All assignments for a specific logistics partner (portal user id)
    @Query("SELECT da FROM DeliveryAssignment da WHERE da.logisticsPartner.portalUser.id = :userId")
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"order", "order.orderDetails", "order.orderDetails.product"})
    List<DeliveryAssignment> findByPartnerPortalUserId(@Param("userId") Integer userId);

    // All assignments for a specific logistics partner entity
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"order", "order.orderDetails", "order.orderDetails.product"})
    List<DeliveryAssignment> findByLogisticsPartnerId(Long partnerId);

    // Active / pending assignments
    List<DeliveryAssignment> findByLogisticsPartnerIdAndStatus(Long partnerId, DeliveryAssignmentStatus status);

    // All assignments for an order
    List<DeliveryAssignment> findByOrderId(Long orderId);
}
