package com.alibou.book.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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
    private ReturnRequest returnRequest;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name")
    private String name;

    @Column(name = "reason")
    private String reason;

    @Column(name = "quantity")
    private String quantity;

    private String RejectionReason;

    private LocalDateTime ProcessedDate;

    @Enumerated(EnumType.STRING)
    private ReturnItemStatus status = ReturnItemStatus.PENDING;

    // Constructors, getters, setters...
}
