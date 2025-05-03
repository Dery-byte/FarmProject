package com.alibou.book.Repositories;

import com.alibou.book.Entity.ReturnRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long> {
    List<ReturnRequest> findByUserId(Long userId);
    Optional<ReturnRequest> findByIdAndUserId(Long id, Long userId);

    List<ReturnRequest> findByItemsCurrentStatus(String status);

    @Query("SELECT r FROM ReturnRequest r JOIN r.items i WHERE i.productId = :productId")
    List<ReturnRequest> findByProductId(@Param("productId") String productId);
}