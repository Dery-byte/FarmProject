package com.alibou.book.Controllers;

import com.alibou.book.Entity.PaymentStatuss;
import com.alibou.book.Services.PaymentStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/auth/payment-status")
public class PaymentStatusController {
private PaymentStatusService paymentStatusService;

    public PaymentStatusController(PaymentStatusService paymentStatusService) {
        this.paymentStatusService = paymentStatusService;
    }

    @PostMapping
    public ResponseEntity<PaymentStatuss> createPaymentStatus(@RequestBody PaymentStatuss status) {
        return ResponseEntity.ok(paymentStatusService.save(status));
    }

    @GetMapping("/getAllPayments")
    public ResponseEntity<List<PaymentStatuss>> getAllPaymentStatuses() {
        return ResponseEntity.ok(paymentStatusService.findAll());
    }

    @GetMapping("/byId/{id}")
    public ResponseEntity<PaymentStatuss> getById(@PathVariable Long id) {
        return paymentStatusService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/external/{externalRef}")
    public ResponseEntity<PaymentStatuss> getByExternalRef(@PathVariable String externalRef) {
        return paymentStatusService.findByExternalRef(externalRef)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        paymentStatusService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
