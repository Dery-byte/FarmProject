package com.alibou.book.Repositories;

import com.alibou.book.Entity.Farm;
import com.alibou.book.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FarmRepository extends JpaRepository<Farm, Long> {
    List<Farm> findByFarmNameContainingIgnoreCase(String farmName);
    List<Farm> findByFarmerId(Long farmerId); // Find farms by user ID

    List<Farm> findByLocationContainingIgnoreCase(String location);




    // Option 1: Using JPA method naming convention

    // Option 2: Using custom JPQL query
    @Query("SELECT f FROM Farm f WHERE LOWER(f.farmName) LIKE LOWER(CONCAT('%', :farmName, '%'))")
    List<Farm> findFarmsByName(@Param("farmName") String farmName);

    // Option 3: To get products directly
    @Query("SELECT p FROM Product p JOIN p.farm f WHERE f.farmName = :farmName")
    List<Product> findProductsByFarmName(@Param("farmName") String farmName);






}
