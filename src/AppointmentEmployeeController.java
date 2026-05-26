package com.petshop.controller;

import com.petshop.domain.dto.AppointmentResponseDTO;
import com.petshop.domain.entity.Appointment;
import com.petshop.domain.enum.ServiceType;
import com.petshop.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/employee/appointments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('EMPLOYEE')")
public class AppointmentEmployeeController {

    private final AppointmentService appointmentService;

    @GetMapping("/today/department/{department}")
    public ResponseEntity<List<AppointmentResponseDTO>> getTodayAppointmentsByDepartment(@PathVariable String department) {
        List<AppointmentResponseDTO> appointments = appointmentService.getTodayAppointmentsByDepartment(department);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/date/{date}/department/{department}")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByDateAndDepartment(
            @PathVariable LocalDate date,
            @PathVariable String department) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<AppointmentResponseDTO> appointments = appointmentService.getAllAppointmentsDTO().stream()
                .filter(a -> a.getScheduledDateTime().toLocalDate().equals(date))
                .filter(a -> {
                    ServiceType serviceType = a.getServiceType();
                    return serviceType.getDepartment().equals(department.toUpperCase());
                })
                .sorted((a1, a2) -> a1.getScheduledDateTime().compareTo(a2.getScheduledDateTime()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/departments")
    public ResponseEntity<List<String>> getAvailableDepartments() {
        return ResponseEntity.ok(List.of("BANHO", "TOSA", "VETERINÁRIO"));
    }

    @PutMapping("/{appointmentId}/start")
    public ResponseEntity<AppointmentResponseDTO> startAppointment(@PathVariable Long appointmentId) {
        Appointment appointment = appointmentService.getAppointmentById(appointmentId);
        appointmentService.confirmAppointment(appointmentId);
        return ResponseEntity.ok(convertToResponseDTO(appointment));
    }

    @PutMapping("/{appointmentId}/complete")
    public ResponseEntity<AppointmentResponseDTO> completeAppointment(@PathVariable Long appointmentId) {
        Appointment appointment = appointmentService.completeAppointment(appointmentId);
        return ResponseEntity.ok(convertToResponseDTO(appointment));
    }

    private AppointmentResponseDTO convertToResponseDTO(Appointment appointment) {
        return AppointmentResponseDTO.builder()
                .id(appointment.getId())
                .animalId(appointment.getAnimal().getId())
                .animalName(appointment.getAnimal().getName())
                .tutorId(appointment.getTutor().getId())
                .tutorName(appointment.getTutor().getName())
                .scheduledDateTime(appointment.getScheduledDateTime())
                .serviceType(appointment.getServiceType())
                .status(appointment.getStatus())
                .price(appointment.getPrice())
                .notes(appointment.getNotes())
                .createdAt(appointment.getCreatedAt())
                .build();
    }
}
