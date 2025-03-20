package com.alibou.book.Entity;

import com.alibou.book.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@Entity
@Table(name = "products")
@Data
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "product_name")  // ✅ Maps to product_name in the database
    private String productName;
    private String description;
    private Double price;
    private Integer quantity;

    private String category; // e.g., Poultry, Meat, Dairy

    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "farm_id")
    private Farm farm; // The seller of the product


//    @JsonIgnore // Ignore serialization of this field
    @ManyToOne(fetch = FetchType.EAGER) // Load user only when needed
    @JoinColumn(name = "farmer_id") // Foreign key in Farm table pointing to User table
    private User farmer; // The seller/owner of the farm
}
