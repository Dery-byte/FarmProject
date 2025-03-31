package com.alibou.book.Controllers;

import com.alibou.book.DTO.PaymentRequest;
import com.alibou.book.Entity.Payment;
import com.alibou.book.Services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/auth/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/{orderId}/make-payment")
    public ResponseEntity<Payment> makePayment(@PathVariable Long orderId, @RequestBody PaymentRequest request, Principal principal) {
        Payment payment = paymentService.processPayment(orderId, request, principal);
        return new ResponseEntity<>(payment, HttpStatus.OK);
    }

}