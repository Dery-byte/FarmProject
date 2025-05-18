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






































































//    PRICING INTERGRATION


    @Query("SELECT MIN(p.price) FROM Product p")
    Double findMinPrice();

    @Query("SELECT MAX(p.price) FROM Product p")
    Double findMaxPrice();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.price BETWEEN :min AND :max")
    Long countByPriceBetween(@Param("min") Double min, @Param("max") Double max);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.price >= :min")
    Long countByPriceGreaterThanEqual(@Param("min") Double min);

    @Query("SELECT COUNT(p) FROM Product p")
    Long countAllProducts();

    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max")
    List<Product> findByPriceRange(@Param("min") Double min, @Param("max") Double max);

    @Query("SELECT p FROM Product p WHERE p.price >= :min")
    List<Product> findByPriceGreaterThanEqual(@Param("min") Double min);
    @Query("SELECT p FROM Product p")
    List<Product> findAllProducts();









    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceBetween(
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice
    );




    @Query("SELECT p FROM Product p WHERE p.price <= :maxPrice")
    List<Product> findByPriceLessThanEqual(@Param("maxPrice") Double maxPrice);




}
