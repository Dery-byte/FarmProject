package com.alibou.book.Services;

import com.alibou.book.Entity.PaymentStatuss;
import com.alibou.book.Repositories.PaymentStatusRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentStatusService {

    private final PaymentStatusRepository paymentStatusRepository;


    public PaymentStatusService(PaymentStatusRepository paymentStatusRepository) {
        this.paymentStatusRepository = paymentStatusRepository;
    }



    public PaymentStatuss save(PaymentStatuss status) {
        return paymentStatusRepository.save(status);
    }

    public List<PaymentStatuss> findAll() {
        return paymentStatusRepository.findAll();
    }

    public Optional<PaymentStatuss> findById(Long id) {
        return paymentStatusRepository.findById(id);
    }

    public Optional<PaymentStatuss> findByExternalRef(String externalRef) {
        return paymentStatusRepository.findByExternalRef(externalRef);
    }

    public void deleteById(Long id) {
        paymentStatusRepository.deleteById(id);
    }
}
