package com.ems.dto;

import com.ems.model.Employee.EmployeeStatus;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDTO {
    private Long id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = "Position is required")
    private String position;

    @DecimalMin(value = "0.0")
    private Double salary;

    private LocalDate dateOfJoining;
    private EmployeeStatus status;
    private String address;
    private String profileImage;
    private String createdAt;
    private String updatedAt;
    private String gender;
}
    