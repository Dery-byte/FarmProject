package com.alibou.book.Repositories;

import com.alibou.book.Entity.PaymentStatuss;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentStatusRepository extends JpaRepository<PaymentStatuss, Long> {
    Optional<PaymentStatuss> findByExternalRef(String externalRef);

    List<PaymentStatuss> findByFarmer_Id(Long farmerId);
}
