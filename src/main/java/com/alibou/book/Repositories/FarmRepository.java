package com.alibou.book.Repositories;

import com.alibou.book.Entity.Farm;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FarmRepository extends JpaRepository<Farm, Long> {
    List<Farm> findByFarmNameContainingIgnoreCase(String farmName);
    List<Farm> findByFarmerId(Long farmerId); // Find farms by user ID

    List<Farm> findByLocationContainingIgnoreCase(String location);
}
