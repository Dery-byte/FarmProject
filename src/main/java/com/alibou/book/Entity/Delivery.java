package com.alibou.book.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
@Data
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deliveryStatus; // "PENDING", "IN TRANSIT", "DELIVERED"
    private String deliveryAddress;
    private LocalDateTime deliveryDate;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
