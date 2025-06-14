package com.alibou.book.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_details")
@Data
@Setter
@Getter
public class OrderDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;
    private double price; // Price at the time of the order

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(20)") // or whatever size you need
    private OrderDetailStatus status = OrderDetailStatus.ACTIVE;





    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    @Enumerated(EnumType.STRING)
    private OrderedItemStatus orderedItemStatus=OrderedItemStatus.PENDING;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("changedAt ASC")
    private List<OrderStatusHistory> orderStatusHistoryList = new ArrayList<>();

//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
//    @OrderBy("changedAt ASC")
//    private List<OrderedItemStatusHistory> orderedItemStatusHistoryList = new ArrayList<>();

    @OneToMany(mappedBy = "orderDetails", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("changedAt ASC")
    private List<OrderedItemStatusHistory> orderedItemStatusHistoryList = new ArrayList<>();


}
