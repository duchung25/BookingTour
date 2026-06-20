package com.spring.project.dto;

public class AdminDashboardMetric {

    private final String value;
    private final String changeText;
    private final String changeCssClass;

    public AdminDashboardMetric(String value, String changeText, String changeCssClass) {
        this.value = value;
        this.changeText = changeText;
        this.changeCssClass = changeCssClass;
    }

    public String getValue() {
        return value;
    }

    public String getChangeText() {
        return changeText;
    }

    public String getChangeCssClass() {
        return changeCssClass;
    }
}
