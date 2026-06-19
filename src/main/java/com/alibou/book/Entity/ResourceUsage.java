package com.alibou.book.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "farm_resource_usage")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ResourceUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_id", nullable = false)
    @JsonIgnore
    private Farm farm;

    // What was used (e.g., Feed Type A, NPK Fertilizer, Water)
    private String resourceName;

    // What it was used on (e.g., Broiler Batch 1, Field A, Greenhouse 2)
    private String usageTarget;

    private Double quantityUsed;
    private String unit;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private LocalDateTime usageDate;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;
}
