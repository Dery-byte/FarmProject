package com.alibou.book.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FarmDTO {
    private Long id;
    private String name;
    private String location;
    private String address;
    private String type;
    private String size;
    private String email;
//    private String description;
    private FarmerDTO farmer;
    private String contact;


}