package com.ems.repository;

import com.ems.model.Attendance;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AttendanceRepository
        extends MongoRepository<Attendance, String> {

    long countByStatusAndAttendanceDate(
            Attendance.AttendanceStatus status,
            LocalDate attendanceDate
    );

    Optional<Attendance> findByEmployeeIdAndAttendanceDate(
            Long employeeId,
            LocalDate attendanceDate
    );
}