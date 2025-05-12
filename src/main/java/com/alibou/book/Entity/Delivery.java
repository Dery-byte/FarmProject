package com.alibou.book.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@Entity
//@Table(name = "deliveries")
//@Data
public class Delivery {
    @Column(nullable = false, length = 100)
    private String recipientName;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false, length = 200)
    private String digitalAddress;

    @Column(length = 200)
    private String street;

    @Column(nullable = false, length = 100)
    private String area;

    @Column(nullable = false, length = 200)
    private String district;

    @Column(nullable = false, length = 50)
    private String region;




    @Column(nullable = false, length = 100)
    private String notes;

    @Column(nullable = false, length = 20)
    private String landmark;

//    @Column(nullable = false, length = 50)
//    private String region;

    // Business logic validation
    public void validate() {
        if (recipientName == null || recipientName.isBlank()) {
            throw new IllegalArgumentException("Recipient name is required");
        }
        // Add other validations as needed
    }
}
