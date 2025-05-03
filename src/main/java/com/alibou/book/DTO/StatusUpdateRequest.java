package com.alibou.book.DTO;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class StatusUpdateRequest {
    @NotBlank
    @Pattern(regexp = "RECEIVED|APPROVED|REJECTED|SHIPPED|REFUNDED")
    private String newStatus;

    // getter, setter
}