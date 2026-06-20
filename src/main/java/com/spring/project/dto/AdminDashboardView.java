package com.spring.project.dto;

import java.util.List;

public class AdminDashboardView {

    private final String todayText;
    private final AdminDashboardMetric totalTours;
    private final AdminDashboardMetric totalBookings;
    private final AdminDashboardMetric totalCustomers;
    private final AdminDashboardMetric monthlyRevenue;
    private final long pendingBookings;
    private final long confirmedBookings;
    private final long completedBookings;
    private final long cancelledBookings;
    private final long upcomingDepartures;
    private final List<AdminDashboardMonthlyRevenue> revenueChart;
    private final List<AdminDashboardActivity> recentActivities;
    private final List<AdminDashboardRecentBooking> recentBookings;

    public AdminDashboardView(
            String todayText,
            AdminDashboardMetric totalTours,
            AdminDashboardMetric totalBookings,
            AdminDashboardMetric totalCustomers,
            AdminDashboardMetric monthlyRevenue,
            long pendingBookings,
            long confirmedBookings,
            long completedBookings,
            long cancelledBookings,
            long upcomingDepartures,
            List<AdminDashboardMonthlyRevenue> revenueChart,
            List<AdminDashboardActivity> recentActivities,
            List<AdminDashboardRecentBooking> recentBookings) {
        this.todayText = todayText;
        this.totalTours = totalTours;
        this.totalBookings = totalBookings;
        this.totalCustomers = totalCustomers;
        this.monthlyRevenue = monthlyRevenue;
        this.pendingBookings = pendingBookings;
        this.confirmedBookings = confirmedBookings;
        this.completedBookings = completedBookings;
        this.cancelledBookings = cancelledBookings;
        this.upcomingDepartures = upcomingDepartures;
        this.revenueChart = revenueChart;
        this.recentActivities = recentActivities;
        this.recentBookings = recentBookings;
    }

    public String getTodayText() {
        return todayText;
    }

    public AdminDashboardMetric getTotalTours() {
        return totalTours;
    }

    public AdminDashboardMetric getTotalBookings() {
        return totalBookings;
    }

    public AdminDashboardMetric getTotalCustomers() {
        return totalCustomers;
    }

    public AdminDashboardMetric getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public long getPendingBookings() {
        return pendingBookings;
    }

    public long getConfirmedBookings() {
        return confirmedBookings;
    }

    public long getCompletedBookings() {
        return completedBookings;
    }

    public long getCancelledBookings() {
        return cancelledBookings;
    }

    public long getUpcomingDepartures() {
        return upcomingDepartures;
    }

    public List<AdminDashboardMonthlyRevenue> getRevenueChart() {
        return revenueChart;
    }

    public List<AdminDashboardActivity> getRecentActivities() {
        return recentActivities;
    }

    public List<AdminDashboardRecentBooking> getRecentBookings() {
        return recentBookings;
    }
}
