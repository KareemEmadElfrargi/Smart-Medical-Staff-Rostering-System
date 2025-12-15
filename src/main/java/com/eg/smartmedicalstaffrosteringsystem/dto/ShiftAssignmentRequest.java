package com.eg.smartmedicalstaffrosteringsystem.dto;

import com.eg.smartmedicalstaffrosteringsystem.entity.ShiftType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShiftAssignmentRequest {
    private Long userId;
    private LocalDate date;
    private ShiftType shiftType;
}
