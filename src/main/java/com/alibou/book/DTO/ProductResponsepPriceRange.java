package com.alibou.book.DTO;

import com.alibou.book.Entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class ProductResponsepPriceRange {

        private List<Product> products;
    private PriceRangeInfo priceRangeInfo;
}
