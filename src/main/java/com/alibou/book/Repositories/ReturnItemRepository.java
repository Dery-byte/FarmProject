package com.alibou.book.Repositories;

import com.alibou.book.Entity.ReturnItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReturnItemRepository extends JpaRepository<ReturnItem, Long> {
    Optional<Object> findByIdAndReturnRequestId(Long itemId, Long returnRequestId);
}