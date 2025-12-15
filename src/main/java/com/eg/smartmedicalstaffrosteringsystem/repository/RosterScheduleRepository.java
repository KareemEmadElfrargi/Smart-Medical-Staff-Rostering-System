package com.eg.smartmedicalstaffrosteringsystem.repository;

import com.eg.smartmedicalstaffrosteringsystem.entity.RosterSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RosterScheduleRepository extends JpaRepository<RosterSchedule, Long> {

    Optional<RosterSchedule> findByUserIdAndShiftDate(Long userId, LocalDate shiftDate);

    @Query("SELECT r FROM RosterSchedule r WHERE r.user.id = :userId AND r.shiftDate BETWEEN :startDate AND :endDate")
    List<RosterSchedule> findByUserIdAndDateRange(@Param("userId") Long userId,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);


    // Find overlapping or adjacent shifts if needed, but primary check is by date
}
