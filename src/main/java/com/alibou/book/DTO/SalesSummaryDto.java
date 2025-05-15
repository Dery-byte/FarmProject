package com.alibou.book.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalesSummaryDto {
    private Double todaySalesRaw;
    private Double weeklyRevenueRaw;
    private String todaySalesFormatted;
    private String weeklyRevenueFormatted;

    // Optional: Add order counts if needed
    private Long todaysOrderCount;
    private Long weeklyOrderCount;
    private LocalDate weekStartDate;  // New field
    private LocalDate weekEndDate;
private String totalUnpaid;

    public SalesSummaryDto(Double totalUnpaid, Double todaySales, Double weeklyRevenue, String s, String s1, LocalDate localDate, LocalDate localDate1) {
    }
}