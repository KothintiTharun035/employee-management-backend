package com.ems.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "attendance")
@CompoundIndex(
    name = "employee_date_unique",
    def = "{'employeeId': 1, 'attendanceDate': 1}",
    unique = true
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

    @Id
    private String id;

    private Long employeeId;

    private LocalDate attendanceDate;

    private AttendanceStatus status;

    public enum AttendanceStatus {
        PRESENT,
        ABSENT,
        LEAVE
    }
}