package com.eg.smartmedicalstaffrosteringsystem.dto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DailyStatusResponse {
    private String date;
    private String status;
    private String timeRange;
    private String message;
}
