package com.alibou.book.Controllers;

import com.alibou.book.DTO.PaymentStatusResponseDTO;
import com.alibou.book.Entity.PaymentStatuss;
import com.alibou.book.Services.PaymentStatusService;
import com.alibou.book.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/auth/payment-status")
public class PaymentStatusController {
private PaymentStatusService paymentStatusService;
    private final UserDetailsService userDetailsService;

    public PaymentStatusController(PaymentStatusService paymentStatusService, UserDetailsService userDetailsService) {
        this.paymentStatusService = paymentStatusService;
        this.userDetailsService = userDetailsService;
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


    @GetMapping("/forFarmer")
    public ResponseEntity<List<PaymentStatusResponseDTO>> paymentForFarmer(Principal principal) {
        if (principal == null) {
            throw new IllegalArgumentException("User must be authenticated to fetch orders.");
        }
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        // Assuming service returns a List<PaymentStatuss>
        List<PaymentStatusResponseDTO> payments = paymentStatusService.paymentForFarmer(Long.valueOf(user.getId()));
        return ResponseEntity.ok(payments);
    }

}
