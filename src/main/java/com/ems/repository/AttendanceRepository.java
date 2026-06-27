package com.ems.repository;

import com.ems.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    long countByStatusAndAttendanceDate(
            Attendance.AttendanceStatus status,
            LocalDate attendanceDate
    );

    @Query("""
        SELECT a
        FROM Attendance a
        WHERE a.employeeId = :employeeId
        AND a.attendanceDate = :attendanceDate
    """)
    Optional<Attendance> findAttendance(
            @Param("employeeId") Long employeeId,
            @Param("attendanceDate") LocalDate attendanceDate
    );
}