package com.spring.project.dto;

public class AdminDashboardMonthlyRevenue {

    private final String monthLabel;
    private final String amountText;
    private final int barHeightPercent;

    public AdminDashboardMonthlyRevenue(String monthLabel, String amountText, int barHeightPercent) {
        this.monthLabel = monthLabel;
        this.amountText = amountText;
        this.barHeightPercent = barHeightPercent;
    }

    public String getMonthLabel() {
        return monthLabel;
    }

    public String getAmountText() {
        return amountText;
    }

    public int getBarHeightPercent() {
        return barHeightPercent;
    }
}
