package com.eg.smartmedicalstaffrosteringsystem.controller;

import com.eg.smartmedicalstaffrosteringsystem.dto.DailyStatusResponse;
import com.eg.smartmedicalstaffrosteringsystem.dto.RosterDTO;
import com.eg.smartmedicalstaffrosteringsystem.dto.ShiftAssignmentRequest;
import com.eg.smartmedicalstaffrosteringsystem.service.RosterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rosters")
@RequiredArgsConstructor
public class RosterController {
    private final RosterService rosterService;

    @GetMapping
    @PreAuthorize("hasRole('HEAD_NURSE')")
    public ResponseEntity<List<RosterDTO>> getRosters(
            @RequestParam(defaultValue = "1") int month,
            @RequestParam(defaultValue = "2025") int year) {
        return ResponseEntity.ok(rosterService.getRosters(month, year));
    }
    // Head Nurse: Assign Shift
    @PostMapping
    @PreAuthorize("hasRole('HEAD_NURSE')")
    public ResponseEntity<Void> assignShift(@RequestBody ShiftAssignmentRequest request) {
        rosterService.assignShift(request);
        return ResponseEntity.ok().build();
    }

    // Staff Nurse: View My Roster
    @GetMapping("/my")
    public ResponseEntity<List<RosterDTO>> getMyRoster(Authentication authentication) {
        String nationalId = authentication.getName();
        return ResponseEntity.ok(rosterService.getMyRoster(nationalId));
    }
    // Staff Nurse: View Daily Status (Dashboard)
    @GetMapping("/daily")
    public ResponseEntity<DailyStatusResponse> getDailyStatus(Authentication authentication) {
        String nationalId = authentication.getName();
        return ResponseEntity.ok(rosterService.getDailyStatus(nationalId));
    }
}
