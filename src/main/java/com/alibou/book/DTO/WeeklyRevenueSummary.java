package com.alibou.book.DTO;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class WeeklyRevenueSummary {
    private int week;
    private double totalAmount;

    public WeeklyRevenueSummary(int week, double totalAmount) {
        this.week = week;
        this.totalAmount = totalAmount;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
