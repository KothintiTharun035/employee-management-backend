package com.ems.model;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    private Long id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Indexed(unique = true)
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = "Position is required")
    private String position;

    @DecimalMin(value = "0.0", message = "Salary must be positive")
    private Double salary;

    private LocalDate dateOfJoining;

    @Builder.Default
    private EmployeeStatus status = EmployeeStatus.ACTIVE;

    private String address;

    private String profileImage;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String gender;

    public enum EmployeeStatus {
        ACTIVE,
        INACTIVE,
        ON_LEAVE
    }
}