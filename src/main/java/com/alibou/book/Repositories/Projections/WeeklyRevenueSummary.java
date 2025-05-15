package com.alibou.book.Repositories.Projections;

public interface WeeklyRevenueSummary {
    Integer getWeek();
    Double getTotalAmount();
}