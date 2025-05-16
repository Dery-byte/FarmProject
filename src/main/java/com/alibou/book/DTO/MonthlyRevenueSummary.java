package com.alibou.book.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class MonthlyRevenueSummary {
    private int month;
    private double totalAmount;

    public MonthlyRevenueSummary(int month, double totalAmount) {
        this.month = month;
        this.totalAmount = totalAmount;
    }

    // Getters and setters
}
