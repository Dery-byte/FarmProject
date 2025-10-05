package com.alibou.book.Entity;

import com.alibou.book.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@Builder
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@AllArgsConstructor
@RequiredArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "product_name")  // ✅ Maps to product_name in the database
    private String productName;
    private String description;
    private Double price;
    private Integer quantity;


    private Double weight;
    private String breed;
    private String healthStatus;

    @Column(name = "`condition`") // Backticks escape the keyword
    private String condition;
    private GenderStatus gender;
    private String age;  // IN months

//    private String imageUrl; // URL pointing to the product image
//@CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
//@Column(name = "image_url")
//private List<String> imageUrls; // ✅ Now stores multiple images


    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();  // I


//    private String category; // e.g., Poultry, Meat, Dairy

    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "farm_id")
    private Farm farm; // The seller of the product


    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category; // The seller of the product

//    @JsonIgnore // Ignore serialization of this field
    @ManyToOne(fetch = FetchType.EAGER) // Load user only when needed
    @JoinColumn(name = "farmer_id") // Foreign key in Farm table pointing to User table
    private User farmer; // The seller/owner of the farm

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<CartItem> cartItems;


    // ✅ Auto-set when inserted
    @CreationTimestamp
    private LocalDateTime createdAt;
}
