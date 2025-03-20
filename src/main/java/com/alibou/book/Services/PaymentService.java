package com.alibou.book.Services;

import com.alibou.book.Entity.Payment;
import com.alibou.book.Repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public Payment processPayment(Payment payment) {
        payment.setStatus("COMPLETED"); // Simulating successful payment
        return paymentRepository.save(payment);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}
