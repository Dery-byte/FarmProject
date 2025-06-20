package com.alibou.book.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryInfoRequest {

    @NotBlank(message = "Recipient name is required")
    private String recipientName;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Digital address is required")
    private String digitalAddress;

    private String street;

    private String area;

    @NotBlank(message = "District is required")
    private String district;

    @NotBlank(message = "Region is required")
    private String region;

    @NotBlank(message = "Notes are required")
    private String notes;

    @NotBlank(message = "Landmark is required")
    private String landmark;
}