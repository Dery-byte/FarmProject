package com.alibou.book.DTO;

import com.alibou.book.role.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FarmerDTO {
    private Integer farmerId;
    private String fullName;
    private String phoneNumber;
    private String email;
    private List<Role> roles;
}
