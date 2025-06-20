package com.alibou.book.DTO;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryResponse {
    private Long id;
    private String recipientName;
    private String phoneNumber;
    private String digitalAddress;
    private String street;
    private String area;
    private String district;
    private String region;
    private String notes;
    private String landmark;
}