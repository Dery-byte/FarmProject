package com.alibou.book.Repositories;

import com.alibou.book.Entity.HealthAndMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthAndMaintenanceRepository extends JpaRepository<HealthAndMaintenance, Long> {
    @Query(value = "SELECT * FROM farm_health_maintenance WHERE farm_id = :farmId", nativeQuery = true)
    List<HealthAndMaintenance> findByFarmId(@Param("farmId") Long farmId);
}
