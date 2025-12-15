package com.eg.smartmedicalstaffrosteringsystem.service;

import com.eg.smartmedicalstaffrosteringsystem.dto.DailyStatusResponse;
import com.eg.smartmedicalstaffrosteringsystem.dto.RosterDTO;
import com.eg.smartmedicalstaffrosteringsystem.dto.ShiftAssignmentRequest;
import com.eg.smartmedicalstaffrosteringsystem.entity.RosterSchedule;
import com.eg.smartmedicalstaffrosteringsystem.entity.ShiftType;
import com.eg.smartmedicalstaffrosteringsystem.entity.User;
import com.eg.smartmedicalstaffrosteringsystem.exception.BusinessValidationException;
import com.eg.smartmedicalstaffrosteringsystem.repository.RosterScheduleRepository;
import com.eg.smartmedicalstaffrosteringsystem.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RosterService {

    private final UserRepository userRepository;
    private final RosterScheduleRepository rosterRepository;

    @Transactional
    public void assignShift(ShiftAssignmentRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessValidationException("User not found"));
        LocalDate date = request.getDate();
        ShiftType newShift = request.getShiftType();

        Optional<RosterSchedule> existing = rosterRepository.findByUserIdAndShiftDate(user.getId(), date);
//        if (existing.isPresent()) {
//            // Requirement implies we might edit, but let's check validation first
//            // If we are editing, we are "overriding".
//        }

        // Rule: Check the Previous Day (Night Shift Rule)
        LocalDate prevDay = date.minusDays(1);
        Optional<RosterSchedule> prevShift = rosterRepository.findByUserIdAndShiftDate(user.getId(), prevDay);
        if (prevShift.isPresent() && prevShift.get().getShiftType() == ShiftType.NIGHT) {
            // The Previous day was NIGHT. Today MUST be OFF or POST_NIGHT_OFF
            if (newShift != ShiftType.OFF && newShift != ShiftType.POST_NIGHT_OFF) {
                throw new BusinessValidationException("Cannot assign a shift because the nurse had a Night shift the previous day.");
            }
        }

        if (newShift == ShiftType.NIGHT) {
            LocalDate nextDay = date.plusDays(1);
            Optional<RosterSchedule> nextShiftOpt = rosterRepository.findByUserIdAndShiftDate(user.getId(), nextDay);

            if (nextShiftOpt.isPresent()) {
                RosterSchedule nextShift = nextShiftOpt.get();
                if (nextShift.getShiftType() != ShiftType.OFF && nextShift.getShiftType() != ShiftType.POST_NIGHT_OFF) {
                    // If the next day has a real shift, we generally block or ask to clear it.
                    // Requirement: "Next day MUST automatically be assigned as 'Off'".
                    // We will overwrite it.
                    nextShift.setShiftType(ShiftType.POST_NIGHT_OFF);
                    rosterRepository.save(nextShift);
                }
            } else {
                RosterSchedule nextSchedule = RosterSchedule.builder()
                        .user(user)
                        .shiftDate(nextDay)
                        .shiftType(ShiftType.POST_NIGHT_OFF)
                        .build();

                rosterRepository.save(nextSchedule);
            }


        }
        // Save current shift
        if (existing.isPresent()) {
            existing.get().setShiftType(newShift);
            rosterRepository.save(existing.get());
        } else {
            RosterSchedule schedule = RosterSchedule.builder()
                    .user(user)
                    .shiftDate(date)
                    .shiftType(newShift)
                    .build();
            rosterRepository.save(schedule);
        }
    }

    public List<RosterDTO> getRosters(int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1); //1
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth()); //30
        // Getting all rosters is complex if we want all users.
        // For simplicity, let's just return all roster entries in range.
        // Ideally we would return a User -> List<Shift> map, but Flat List is easier for JSON.
        // User requirements: "Rows: Nurses, Columns: Days". Frontend can pivot.

        // Since we don't have a "Find ALL in Range" in repo, let's use findAll and filter or add a custom query.
        // Added a custom query `findByUserIdAndDateRange` previously.
        // Let's assume we fetch all for all users.
        List<RosterSchedule> allSchedules = rosterRepository.findAll().stream()
                .filter(r -> !r.getShiftDate().isBefore(start) && !r.getShiftDate().isAfter(end))
                .toList();
        return allSchedules.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<RosterDTO> getMyRoster(String nationalId) {
        User user = userRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new BusinessValidationException("User not found"));
        // Return current month or all? Let's return upcoming.
        // Requirement: "daily and monthly views".
        // Let's return current month.

        LocalDate now = LocalDate.now();
        LocalDate start = now.withDayOfMonth(1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return rosterRepository.findByUserIdAndDateRange(user.getId(), start, end)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public DailyStatusResponse getDailyStatus(String nationalId) {
        User user = userRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new BusinessValidationException("User not found"));

        LocalDate today = LocalDate.now();
        //LocalTime nowTime = LocalTime.now();

        Optional<RosterSchedule> todayShiftOpt = rosterRepository.findByUserIdAndShiftDate(user.getId(), today);
        // Check "Post-Night" Logic specifically for viewing
        // If today is OFF/POST_NIGHT_OFF, checked yesterday
        if (todayShiftOpt.isPresent() &&
                (todayShiftOpt.get().getShiftType() ==
                        ShiftType.POST_NIGHT_OFF || todayShiftOpt.get().getShiftType() ==
                        ShiftType.OFF)) {
            LocalDate yesterday = today.minusDays(1);
            Optional<RosterSchedule> yesterdayShift = rosterRepository.findByUserIdAndShiftDate(user.getId(), yesterday);
            if (yesterdayShift.isPresent() && yesterdayShift.get().getShiftType() == ShiftType.NIGHT) {
                // It's a post-night rest

                return DailyStatusResponse.builder()
                        .date(today.toString())
                        .status("Post-Night Rest")
                        .message("You are on mandatory rest today after completing a Night shift.")
                        .timeRange("REST")
                        .build();
            }

        }

        if (todayShiftOpt.isEmpty()) {
            return DailyStatusResponse.builder()
                    .date(today.toString())
                    .status("Not Scheduled")
                    .message("No shift assigned for today.")
                    .timeRange("-")
                    .build();
        }


        RosterSchedule shift = todayShiftOpt.get();
        return DailyStatusResponse.builder()
                .date(today.toString())
                .status(shift.getShiftType().name())
                .timeRange(shift.getShiftType().getStartTime() + " - " + shift.getShiftType().getEndTime())
                .message("You are assigned to " + shift.getShiftType().name())
                .build();
    }


    private RosterDTO mapToDTO(RosterSchedule r) {
        return RosterDTO.builder()
                .id(r.getId())
                .date(r.getShiftDate())
                .shiftType(r.getShiftType())
                .nurseName(r.getUser().getFullName())
                .nurseId(r.getUser().getId())
                .build();
    }
}


