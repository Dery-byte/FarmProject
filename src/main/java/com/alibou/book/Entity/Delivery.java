package com.alibou.book.Entity;

import com.alibou.book.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_info")  // Explicitly define the table name
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, length = 100)
    private String recipientName;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false, length = 200)
    private String digitalAddress;

    @Column(length = 200)
    private String street;

    @Column(length = 100)
    private String area = "Accra";

    @Column(nullable = false, length = 200)
    private String district;

    @Column(nullable = false, length = 50)
    private String region;

    @Column(nullable = false, length = 100)
    private String notes;

    @Column(nullable = false, length = 20)
    private String landmark;


    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public void validate() {
        if (recipientName == null || recipientName.isBlank()) {
            throw new IllegalArgumentException("Recipient name is required");
        }
        // Add other validations as needed
    }
}