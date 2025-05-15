package com.alibou.book.DTO;

import lombok.Data;

import java.util.Set;

@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@RequiredArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private Set<String> roles;

    public UserResponseDTO(Integer id, String username, Set<String> roles) {
        this.id = Long.valueOf(id);
        this.username = username;
        this.roles = roles;
    }

}
