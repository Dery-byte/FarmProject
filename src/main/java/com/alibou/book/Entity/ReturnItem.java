package com.alibou.book.Entity;

import com.alibou.book.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "return_items")
public class ReturnItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_request_id", nullable = false)
    @JsonIgnore
    private ReturnRequest returnRequest;

//    @Column(name = "product_id", nullable = false)
//    private Long productId;

    @Column(name = "product_name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;  // This is the crucial relationship

//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
//    private User customerName;  // This is the crucial relationship



    @Column(name = "reason")
    private String reason;
    @Column(name = "quantity")
    private String quantity;
    private String RejectionReason;
//    private String image;
    private LocalDateTime ProcessedDate;
    @Enumerated(EnumType.STRING)
    private ReturnItemStatus status = ReturnItemStatus.PENDING;
    private String currentStatus;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("changedAt ASC")
    private List<StatusHistory> statusHistory = new ArrayList<>();

}
