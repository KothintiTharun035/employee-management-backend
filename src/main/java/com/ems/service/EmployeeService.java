package com.ems.service;

import com.ems.dto.EmployeeDTO;
import com.ems.model.Attendance;
import com.ems.model.Employee;
import com.ems.model.Employee.EmployeeStatus;
import com.ems.repository.AttendanceRepository;
import com.ems.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public EmployeeDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Employee not found with id: " + id
                        )
                );

        return toDTO(employee);
    }

    public EmployeeDTO createEmployee(EmployeeDTO dto) {

        if (employeeRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException(
                    "Employee with email " +
                    dto.getEmail() +
                    " already exists"
            );
        }

        Employee employee = toEntity(dto);

        employee.setId(generateNextEmployeeId());

        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);

        Employee savedEmployee =
                employeeRepository.save(employee);

        return toDTO(savedEmployee);
    }

    public EmployeeDTO updateEmployee(Long id, EmployeeDTO dto) {

        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Employee not found with id: " + id
                        )
                );

        employeeRepository.findByEmail(dto.getEmail())
                .ifPresent(employee -> {
                    if (!employee.getId().equals(id)) {
                        throw new RuntimeException(
                                "Email already in use by another employee"
                        );
                    }
                });

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setEmail(dto.getEmail());
        existing.setPhoneNumber(dto.getPhoneNumber());
        existing.setDepartment(dto.getDepartment());
        existing.setPosition(dto.getPosition());
        existing.setSalary(dto.getSalary());
        existing.setDateOfJoining(dto.getDateOfJoining());

        existing.setStatus(
                dto.getStatus() != null
                        ? dto.getStatus()
                        : existing.getStatus()
        );

        existing.setAddress(dto.getAddress());
        existing.setGender(dto.getGender());

        existing.setUpdatedAt(LocalDateTime.now());

        Employee updatedEmployee =
                employeeRepository.save(existing);

        return toDTO(updatedEmployee);
    }

    public void deleteEmployee(Long id) {

        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException(
                    "Employee not found with id: " + id
            );
        }

        employeeRepository.deleteById(id);
    }

    public List<EmployeeDTO> searchEmployees(String keyword) {

        String searchKeyword =
                keyword == null
                        ? ""
                        : keyword.toLowerCase().trim();

        return employeeRepository.findAll()
                .stream()
                .filter(employee ->
                        containsIgnoreCase(
                                employee.getFirstName(),
                                searchKeyword
                        )
                        ||
                        containsIgnoreCase(
                                employee.getLastName(),
                                searchKeyword
                        )
                        ||
                        containsIgnoreCase(
                                employee.getEmail(),
                                searchKeyword
                        )
                        ||
                        containsIgnoreCase(
                                employee.getDepartment(),
                                searchKeyword
                        )
                        ||
                        containsIgnoreCase(
                                employee.getPosition(),
                                searchKeyword
                        )
                        ||
                        String.valueOf(employee.getId())
                                .contains(searchKeyword)
                )
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<EmployeeDTO> getEmployeesByDepartment(
            String department
    ) {

        return employeeRepository
                .findByDepartment(department)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<String> getAllDepartments() {

        return employeeRepository.findAll()
                .stream()
                .map(Employee::getDepartment)
                .filter(Objects::nonNull)
                .filter(department ->
                        !department.isBlank()
                )
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public Map<String, Object> getDashboardStats() {

        Map<String, Object> stats = new HashMap<>();

        stats.put(
                "totalEmployees",
                employeeRepository.count()
        );

        stats.put(
                "activeEmployees",
                employeeRepository.countByStatus(
                        EmployeeStatus.ACTIVE
                )
        );

        stats.put(
                "inactiveEmployees",
                employeeRepository.countByStatus(
                        EmployeeStatus.INACTIVE
                )
        );

        stats.put(
                "onLeave",
                employeeRepository.countByStatus(
                        EmployeeStatus.ON_LEAVE
                )
        );

        stats.put(
                "departments",
                getAllDepartments()
        );

        double totalPayroll =
                employeeRepository.findAll()
                        .stream()
                        .map(Employee::getSalary)
                        .filter(Objects::nonNull)
                        .mapToDouble(Double::doubleValue)
                        .sum();

        stats.put(
                "totalPayroll",
                totalPayroll
        );

        LocalDate today = LocalDate.now();

        stats.put(
                "presentToday",
                attendanceRepository
                        .countByStatusAndAttendanceDate(
                                Attendance.AttendanceStatus.PRESENT,
                                today
                        )
        );

        stats.put(
                "absentToday",
                attendanceRepository
                        .countByStatusAndAttendanceDate(
                                Attendance.AttendanceStatus.ABSENT,
                                today
                        )
        );

        stats.put(
                "leaveToday",
                attendanceRepository
                        .countByStatusAndAttendanceDate(
                                Attendance.AttendanceStatus.LEAVE,
                                today
                        )
        );

        return stats;
    }

    private Long generateNextEmployeeId() {

        return employeeRepository
                .findAll(
                        Sort.by(
                                Sort.Direction.DESC,
                                "id"
                        )
                )
                .stream()
                .map(Employee::getId)
                .filter(Objects::nonNull)
                .findFirst()
                .map(id -> id + 1)
                .orElse(1L);
    }

    private boolean containsIgnoreCase(
            String value,
            String keyword
    ) {

        return value != null
                && value.toLowerCase()
                        .contains(keyword);
    }

    private EmployeeDTO toDTO(Employee employee) {

        return EmployeeDTO.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .phoneNumber(employee.getPhoneNumber())
                .department(employee.getDepartment())
                .position(employee.getPosition())
                .salary(employee.getSalary())
                .dateOfJoining(employee.getDateOfJoining())
                .status(employee.getStatus())
                .address(employee.getAddress())
                .gender(employee.getGender())
                .createdAt(
                        employee.getCreatedAt() != null
                                ? employee.getCreatedAt()
                                    .format(FORMATTER)
                                : null
                )
                .updatedAt(
                        employee.getUpdatedAt() != null
                                ? employee.getUpdatedAt()
                                    .format(FORMATTER)
                                : null
                )
                .build();
    }

    private Employee toEntity(EmployeeDTO dto) {

        return Employee.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .department(dto.getDepartment())
                .position(dto.getPosition())
                .salary(dto.getSalary())
                .dateOfJoining(dto.getDateOfJoining())
                .status(
                        dto.getStatus() != null
                                ? dto.getStatus()
                                : EmployeeStatus.ACTIVE
                )
                .address(dto.getAddress())
                .gender(dto.getGender())
                .build();
    }
}