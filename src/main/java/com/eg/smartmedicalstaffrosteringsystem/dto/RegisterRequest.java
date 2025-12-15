package com.eg.smartmedicalstaffrosteringsystem.dto;

import com.eg.smartmedicalstaffrosteringsystem.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String fullName;
    private String nationalId;
    private String mobileNumber;
    private String department;
    private String password;
    private Role role; // Optional, default to STAFF_NURSE if null
}
