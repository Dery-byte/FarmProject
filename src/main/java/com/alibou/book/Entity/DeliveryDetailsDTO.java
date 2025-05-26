package com.alibou.book.Entity;

import lombok.Data;

@Data
public class DeliveryDetailsDTO {
    private String recipient;
    private String phone;
    private String gpsAddress;
    private String street;
    private String area;
    private String district;
    private String region;
    private String deliveryNote;
    private String majorLandmark;
}
