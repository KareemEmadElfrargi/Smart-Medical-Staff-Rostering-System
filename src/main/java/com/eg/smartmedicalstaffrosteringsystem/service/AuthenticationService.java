package com.eg.smartmedicalstaffrosteringsystem.service;

import com.eg.smartmedicalstaffrosteringsystem.dto.AuthResponse;
import com.eg.smartmedicalstaffrosteringsystem.dto.LoginRequest;
import com.eg.smartmedicalstaffrosteringsystem.dto.RegisterRequest;
import com.eg.smartmedicalstaffrosteringsystem.entity.Role;
import com.eg.smartmedicalstaffrosteringsystem.entity.User;
import com.eg.smartmedicalstaffrosteringsystem.repository.UserRepository;
import com.eg.smartmedicalstaffrosteringsystem.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public AuthResponse authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getNationalId(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByNationalId(request.getNationalId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .name(user.getFullName())
                .role(user.getRole())
                .nationalId(user.getNationalId())
                .build();
    }


    public void register(RegisterRequest request) {
        if (userRepository.existsByNationalId(request.getNationalId())) {
            throw new RuntimeException("User with this National ID already exists");
        }

        var user = User.builder()
                .fullName(request.getFullName())
                .nationalId(request.getNationalId())
                .mobileNumber(request.getMobileNumber())
                .department(request.getDepartment())
                .role(request.getRole() != null ? request.getRole() : Role.STAFF_NURSE)
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
    }
}
