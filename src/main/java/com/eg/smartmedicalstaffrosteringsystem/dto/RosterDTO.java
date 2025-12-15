package com.eg.smartmedicalstaffrosteringsystem.dto;

import com.eg.smartmedicalstaffrosteringsystem.entity.ShiftType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class RosterDTO {
    private Long id;
    private LocalDate date;
    private ShiftType shiftType;
    private String nurseName;
    private Long nurseId;
}

