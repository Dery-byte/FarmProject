package com.alibou.book.Repositories;

import com.alibou.book.Entity.Delivery;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}