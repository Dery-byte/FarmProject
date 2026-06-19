package com.alibou.book.Entity;

import com.alibou.book.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_assignment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class DeliveryAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"logisticsPartner", "customer", "payment", "orderStatusHistoryList"})
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "logistics_partner_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"assignments", "portalUser"})
    private LogisticsPartner logisticsPartner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User assignedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryAssignmentStatus status = DeliveryAssignmentStatus.PENDING;

    // Delivery details (filled when assigning)
    private String pickupLocation;
    private String destinationAddress;
    private String animalType;
    private String quantityOrWeight;
    private String specialRequirements;

    // Financial
    private BigDecimal agreedCharge;

    // Partner feedback
    @Column(length = 500)
    private String partnerNotes;
    private String proofOfDeliveryUrl;

    // Timestamps
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime assignedAt;

    private LocalDateTime respondedAt;
    private LocalDateTime pickedUpAt;
    private LocalDateTime deliveredAt;
}
