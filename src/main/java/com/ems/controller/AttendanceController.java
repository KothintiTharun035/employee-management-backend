package com.ems.controller;

import com.ems.model.Attendance;
import com.ems.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AttendanceController {

    private final AttendanceRepository attendanceRepository;

    @GetMapping
    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    @PostMapping
    public Attendance markAttendance(@RequestBody Attendance attendance) {

    Attendance existing = attendanceRepository.findAttendance(
                    attendance.getEmployeeId(),
                    attendance.getAttendanceDate()
            )
            .orElse(null);

    if (existing != null) {
        existing.setStatus(attendance.getStatus());
        return attendanceRepository.save(existing);
    }

    return attendanceRepository.save(attendance);
}
}