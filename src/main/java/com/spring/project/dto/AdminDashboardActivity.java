package com.spring.project.dto;

import java.time.LocalDateTime;

public class AdminDashboardActivity {

    private final String title;
    private final String description;
    private final String badgeClass;
    private final LocalDateTime occurredAt;

    public AdminDashboardActivity(String title, String description, String badgeClass, LocalDateTime occurredAt) {
        this.title = title;
        this.description = description;
        this.badgeClass = badgeClass;
        this.occurredAt = occurredAt;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getBadgeClass() {
        return badgeClass;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
}
