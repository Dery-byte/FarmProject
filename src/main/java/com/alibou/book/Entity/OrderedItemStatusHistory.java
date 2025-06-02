package com.alibou.book.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter
@Entity
public class OrderedItemStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String status;
    private LocalDateTime changedAt;
    private String changedBy; // Admin username or system
    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private OrderDetails orderDetails;
}
