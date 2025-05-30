package com.alibou.book.Entity;

import com.alibou.book.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.lang.Long;
import java.util.List;

@Entity
@Table(name = "farms")
@Data
public class Farm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long farm_id;

    private String farmName;
    private String location;
    private String contact;
    private String email;
    @Column(name = "farm_address", columnDefinition = "TEXT")
    private String farmAddress;
    private String farmOwner;
    private String farmSize;
    private String farmType;
    private String yearEstablished;
    private String numberOfworkers;

    @ManyToOne(fetch = FetchType.EAGER) // Load user only when needed
    @JoinColumn(name = "farmer_id") // Foreign key in Farm table pointing to User table
    private User farmer; // The seller/owner of the farm


    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL) // Fix: mappedBy should reference the field in Product
    @JsonIgnore
    private List<Product> productList;
}
