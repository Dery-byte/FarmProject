package com.alibou.book.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryInfoRequest {
    @NotBlank private String recipientName;
    @NotBlank private String phoneNumber;
    @NotBlank private String digitalAddress;

//    private String addressLine2;


//    @NotBlank private String city;
//    @NotBlank private String postalCode;

    @NotBlank private String street;
    @NotBlank private String area;

    @NotBlank private String district;

    @NotBlank private String region;
    @NotBlank private String notes;
    @NotBlank private String landmark;
}
