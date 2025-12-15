package com.eg.smartmedicalstaffrosteringsystem.controller;

import com.eg.smartmedicalstaffrosteringsystem.dto.RegisterRequest;
import com.eg.smartmedicalstaffrosteringsystem.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final AuthenticationService authenticationService;

    @PostMapping
    @PreAuthorize("hasRole('HEAD_NURSE')")
    public ResponseEntity<Void> registerUser(@RequestBody RegisterRequest request) {
        authenticationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
