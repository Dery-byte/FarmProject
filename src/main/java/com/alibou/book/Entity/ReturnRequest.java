package com.alibou.book.Entity;

import com.alibou.book.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "return_requests")
public class ReturnRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "overall_reason")
    private String reason;

    @OneToMany(mappedBy = "returnRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReturnItem> items = new ArrayList<>();








    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "return_request_Date")
    private LocalDateTime RequestDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ReturnStatus status = ReturnStatus.PENDING;

    // Constructors, getters, setters
    public ReturnRequest() {
//        this.createdAt = LocalDateTime.now();
    }

    // Add item helper method
    public void addItem(ReturnItem item) {
        items.add(item);
        item.setReturnRequest(this);
    }

    // Getters and setters...
}