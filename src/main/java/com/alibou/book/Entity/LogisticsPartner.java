package com.alibou.book.Entity;

import com.alibou.book.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "logistics_partner")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class LogisticsPartner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String partnerName;
    private String companyName;
    private String contactName;
    private String contactPhone;
    private String contactEmail;

    // Free-form entry
    private String vehicleType;
    private String trackAndTraceUrl;
    private String region;

    // Capacity & rates
    private BigDecimal capacityKg;
    private BigDecimal baseRatePerKm;
    private BigDecimal flatRate;

    private boolean isGlobal; // true = Admin-added (visible to all), false = Farmer-private

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    @JsonIgnore
    private User createdBy;

    // Portal login account (auto-generated when partner is registered)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "portal_user_id")
    @JsonIgnore
    private User portalUser;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;
}

