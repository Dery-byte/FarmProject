package com.alibou.book.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // E.g., "Mobile Money", "Bank Transfer"

    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // "PENDING", "COMPLETED", "FAILED"

    private String transactionId;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;
    private LocalDateTime paymentDate = LocalDateTime.now();

}
