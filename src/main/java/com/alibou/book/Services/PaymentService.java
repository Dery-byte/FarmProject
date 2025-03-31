package com.alibou.book.Services;

import com.alibou.book.DTO.PaymentRequest;
import com.alibou.book.Entity.Order;
import com.alibou.book.Entity.Payment;
import com.alibou.book.Entity.PaymentMethod;
import com.alibou.book.Entity.PaymentStatus;
import com.alibou.book.Repositories.OrderRepository;
import com.alibou.book.Repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;

import static com.alibou.book.Entity.OrderStatus.COMPLETED;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public Payment processPayment(Long orderId, PaymentRequest request, Principal principal) {
        // Optional: validate that this order belongs to the logged-in user
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.isPaid()) {
            throw new RuntimeException("Order already paid");
        }

        Payment payment = new Payment();
        payment.setAmount(order.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus(PaymentStatus.COMPLETED); // Assume success for now
        payment.setPaymentDate(LocalDateTime.now());
        payment.setTransactionId(request.getTransactionId());
        payment.setOrder(order);

        order.setPaid(true);
        order.setStatus(COMPLETED);

        paymentRepository.save(payment);
        orderRepository.save(order);

        return payment;
    }
}
