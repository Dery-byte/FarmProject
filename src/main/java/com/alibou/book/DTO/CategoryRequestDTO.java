package com.alibou.book.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequestDTO {
    private Long id;
    private String categoryName;
    private String categoryDescription;
    private String userRole;
}
