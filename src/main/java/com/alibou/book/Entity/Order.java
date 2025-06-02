package com.alibou.book.Entity;

import com.alibou.book.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@Data
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime orderDate;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    private Double amount;
    private boolean isPaid;

//    @Enumerated(EnumType.STRING)
//    private String status;


    @Enumerated(EnumType.STRING)
    private OrderStatus status;


    @Enumerated(EnumType.STRING)
    private OrdersStatus ordersStatus;



    @ManyToOne
    @JoinColumn(name = "user_id")
    private User customer;

    @Embedded
    private Delivery deliveryInfo;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,  fetch = FetchType.LAZY)
    private List<OrderDetails> orderDetails;


    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonIgnore
    private Payment payment;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("changedAt ASC")
    @JsonManagedReference
    private List<OrderStatusHistory> orderStatusHistoryList= new ArrayList<>();
}


