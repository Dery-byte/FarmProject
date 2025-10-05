package com.alibou.book.Mapper;

import com.alibou.book.Entity.Product;
import com.alibou.book.DTO.LatestProductDTO;

public class ProductMapper {
    public static LatestProductDTO toDTO(Product product) {
        return LatestProductDTO.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .weight(product.getWeight())
                .breed(product.getBreed())
                .healthStatus(product.getHealthStatus())
                .condition(product.getCondition())
                .gender(product.getGender() != null ? product.getGender().name() : null)
                .age(product.getAge())
                .imageUrls(product.getImageUrls())
                .category(product.getCategory() != null ? product.getCategory().getCategoryName() : null)
                .farmName(product.getFarm() != null ? product.getFarm().getFarmName() : null)
                .farmLocation(product.getFarm() != null ? product.getFarm().getLocation() : null)
                .farmerName(product.getFarmer() != null ? product.getFarmer().getFullName() : null)
                .farmerContact(product.getFarmer() != null ? product.getFarmer().getPhoneNummber() : null)
                .createdAt(product.getCreatedAt())
                .build();
    }
}
