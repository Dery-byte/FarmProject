package com.alibou.book.DTO;

import lombok.Data;

@Data
public class DeliveryDTO {
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
