package com.alibou.book.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class MonthlyReturnSummary {
    private int month;
    private long totalReturns;

    public MonthlyReturnSummary(int month, long totalReturns) {
        this.month = month;
        this.totalReturns = totalReturns;
    }

    public int getMonth() {
        return month;
    }

    public long getTotalReturns() {
        return totalReturns;
    }
}
