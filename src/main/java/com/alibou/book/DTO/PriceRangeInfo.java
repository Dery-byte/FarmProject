package com.alibou.book.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class PriceRangeInfo {
    private Double minPrice;
    private Double maxPrice;
    private List<PriceRange> ranges;

    @Data
    @AllArgsConstructor
    public static class PriceRange {
        private Double min;
        private Double max;
        private Long productCount;
        private String displayLabel;
    }
}

