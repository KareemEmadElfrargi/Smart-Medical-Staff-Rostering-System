package com.eg.smartmedicalstaffrosteringsystem.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter

public enum ShiftType {
    MORNING("09:00 AM", "02:00 PM"),
    FULL_DAY("09:00 AM", "09:00 PM"),
    NIGHT("09:00 PM", "09:00 AM"), // Next day
    OFF("00:00", "00:00"),
    POST_NIGHT_OFF("00:00", "00:00"); // Special off type

    private final String startTime;
    private final String endTime;

    ShiftType(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
