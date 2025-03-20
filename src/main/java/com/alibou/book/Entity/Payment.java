package com.alibou.book.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "payments")
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;
    private String paymentMethod; // E.g., "Mobile Money", "Bank Transfer"
    private String status; // "PENDING", "COMPLETED", "FAILED"

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
