package com.alibou.book.Repositories;

import com.alibou.book.Entity.ResourceUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceUsageRepository extends JpaRepository<ResourceUsage, Long> {
    @Query(value = "SELECT * FROM farm_resource_usage WHERE farm_id = :farmId", nativeQuery = true)
    List<ResourceUsage> findByFarmId(@Param("farmId") Long farmId);
}
