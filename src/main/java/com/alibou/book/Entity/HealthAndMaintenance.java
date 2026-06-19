package com.alibou.book.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "farm_health_maintenance")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class HealthAndMaintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_id", nullable = false)
    @JsonIgnore
    private Farm farm;

    // What is the activity (e.g., VACCINATION, PESTICIDE_SPRAY, CHECKUP, DEWORMING)
    private String activityType;

    // What was it performed on (e.g., Broiler Batch 1, Field A)
    private String targetEntity;

    // Specific name (e.g., Newcastle Vaccine, Roundup Herbicide)
    private String activityName;

    private LocalDate datePerformed;
    private LocalDate nextDueDate;

    // e.g., COMPLETED, PENDING, CANCELLED
    private String status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;
}
