package com.ems.service;

import com.ems.dto.EmployeeDTO;
import com.ems.model.Attendance;
import com.ems.model.Employee;
import com.ems.model.Employee.EmployeeStatus;
import com.ems.repository.AttendanceRepository;
import com.ems.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public EmployeeDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        return toDTO(employee);
    }

    public EmployeeDTO createEmployee(EmployeeDTO dto) {
        if (employeeRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Employee with email " + dto.getEmail() + " already exists");
        }
        Employee employee = toEntity(dto);
        return toDTO(employeeRepository.save(employee));
    }

    public EmployeeDTO updateEmployee(Long id, EmployeeDTO dto) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        // Check email uniqueness (excluding current employee)
        employeeRepository.findByEmail(dto.getEmail())
                .ifPresent(e -> {
                    if (!e.getId().equals(id)) {
                        throw new RuntimeException("Email already in use by another employee");
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
        existing.setStatus(dto.getStatus() != null ? dto.getStatus() : existing.getStatus());
        existing.setAddress(dto.getAddress());
existing.setGender(dto.getGender());

// Remove this if you're no longer using profile images
// existing.setProfileImage(dto.getProfileImage());

return toDTO(employeeRepository.save(existing));
    }

    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
    }

    public List<EmployeeDTO> searchEmployees(String keyword) {
        return employeeRepository.searchEmployees(keyword)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<EmployeeDTO> getEmployeesByDepartment(String department) {
        return employeeRepository.findByDepartment(department)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<String> getAllDepartments() {
        return employeeRepository.findAllDepartments();
    }

    public Map<String, Object> getDashboardStats() {
    Map<String, Object> stats = new HashMap<>();
    stats.put("totalEmployees", employeeRepository.count());
    stats.put("activeEmployees", employeeRepository.countByStatus(EmployeeStatus.ACTIVE));
    stats.put("inactiveEmployees", employeeRepository.countByStatus(EmployeeStatus.INACTIVE));
    stats.put("onLeave", employeeRepository.countByStatus(EmployeeStatus.ON_LEAVE));
    stats.put("departments", employeeRepository.findAllDepartments());
    stats.put("totalPayroll", employeeRepository.getTotalPayroll());

    LocalDate today = LocalDate.now();
    
    stats.put(
    "presentToday",
    attendanceRepository.countByStatusAndAttendanceDate(
        Attendance.AttendanceStatus.PRESENT,
        today
    )
);

stats.put(
    "absentToday",
    attendanceRepository.countByStatusAndAttendanceDate(
        Attendance.AttendanceStatus.ABSENT,
        today
    )
);

stats.put(
    "leaveToday",
    attendanceRepository.countByStatusAndAttendanceDate(
        Attendance.AttendanceStatus.LEAVE,
        today
    )
);
    return stats;
}

    // --- Mappers ---

    private EmployeeDTO toDTO(Employee e) {
        return EmployeeDTO.builder()
                .id(e.getId())
                .firstName(e.getFirstName())
                .lastName(e.getLastName())
                .email(e.getEmail())
                .phoneNumber(e.getPhoneNumber())
                .department(e.getDepartment())
                .position(e.getPosition())
                .salary(e.getSalary())
                .dateOfJoining(e.getDateOfJoining())
                .status(e.getStatus())
                .address(e.getAddress())
                .gender(e.getGender())

                .createdAt(e.getCreatedAt() != null ? e.getCreatedAt().format(FORMATTER) : null)
                .updatedAt(e.getUpdatedAt() != null ? e.getUpdatedAt().format(FORMATTER) : null)
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
                .status(dto.getStatus() != null ? dto.getStatus() : EmployeeStatus.ACTIVE)
                .address(dto.getAddress())
                .gender(dto.getGender())

// Remove if not using profile images
// .profileImage(dto.getProfileImage())

                .build();
    }
}
