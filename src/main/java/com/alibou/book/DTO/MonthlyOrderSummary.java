package com.alibou.book.DTO;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class MonthlyOrderSummary {
    private int month;
    private long totalOrders;

    public MonthlyOrderSummary(int month, long totalOrders) {
        this.month = month;
        this.totalOrders = totalOrders;
    }

    public int getMonth() {
        return month;
    }

    public long getTotalOrders() {
        return totalOrders;
    }
}
