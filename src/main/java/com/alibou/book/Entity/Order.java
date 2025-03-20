package com.alibou.book.Entity;

import com.alibou.book.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private Double amount;
    private boolean isPaid;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User customer;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)

    private List<OrderDetails> orderDetails;
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;
}
