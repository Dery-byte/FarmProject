package com.alibou.book.Repositories;

import com.alibou.book.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByProductNameContainingIgnoreCase(String productName);
    List<Product> findByFarm_LocationContainingIgnoreCase(String location);
    List<Product> findByCategory(String category);





    @Query("SELECT p FROM Product p WHERE p.farm.location = :location")
    List<Product> findByFarmLocation(@Param("location") String location);

    // Alternative approach if you need more flexibility
    List<Product> findByFarm_Location(String location);
}
