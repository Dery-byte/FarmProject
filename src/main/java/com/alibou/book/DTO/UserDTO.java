package com.alibou.book.DTO;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public class UserDTO {
    private Integer id;
    private String firstname;
    private String lastname;
    private String username;
    private String phoneNummber;
    private LocalDateTime createdDate;
    private boolean enabled;
    private List<String> roles;
    private List<String> authorities;
}
