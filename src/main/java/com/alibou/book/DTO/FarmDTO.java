package com.alibou.book.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FarmDTO {
    private Long id;
    private String name;
    private String location;
    private String address;
    private String type;
    private String size;
}