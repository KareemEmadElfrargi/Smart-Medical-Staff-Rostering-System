package com.eg.smartmedicalstaffrosteringsystem.dto;

import com.eg.smartmedicalstaffrosteringsystem.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String name;
    private Role role;
    private String nationalId;
}
