package com.alibou.book.Repositories;

import com.alibou.book.Entity.FarmInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FarmInventoryRepository extends JpaRepository<FarmInventory, Long> {
    @Query(value = "SELECT * FROM farm_inventory WHERE farm_id = :farmId", nativeQuery = true)
    List<FarmInventory> findByFarmId(@Param("farmId") Long farmId);
}
