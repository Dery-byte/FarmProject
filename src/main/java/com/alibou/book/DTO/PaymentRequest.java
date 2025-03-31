package com.alibou.book.DTO;

import com.alibou.book.Entity.PaymentMethod;
import lombok.Data;

@Data
public class PaymentRequest {
    private PaymentMethod paymentMethod;
    private String transactionId; // Optional
}
