package com.eg.smartmedicalstaffrosteringsystem.controller;


import com.eg.smartmedicalstaffrosteringsystem.dto.AuthResponse;
import com.eg.smartmedicalstaffrosteringsystem.dto.LoginRequest;
import com.eg.smartmedicalstaffrosteringsystem.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService service;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }
}
