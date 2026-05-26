package com.petshop.controller;

import com.petshop.domain.dto.AppointmentRequestDTO;
import com.petshop.domain.dto.AppointmentResponseDTO;
import com.petshop.domain.dto.CancellationRequestDTO;
import com.petshop.domain.entity.Appointment;
import com.petshop.domain.enum.ServiceType;
import com.petshop.exception.CancellationNotAllowedException;
import com.petshop.service.AppointmentService;
import com.petshop.service.AppointmentAvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/public/appointments")
@RequiredArgsConstructor
public class AppointmentPublicController {

    private final AppointmentService appointmentService;
    private final AppointmentAvailabilityService availabilityService;

    @GetMapping("/services")
    public ResponseEntity<List<ServiceTypeResponse>> getAvailableServices() {
        List<ServiceTypeResponse> services = List.of(ServiceType.values()).stream()
                .map(s -> new ServiceTypeResponse(s.name(), s.getLabel(), s.getPrice(), s.getCategory()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(services);
    }

    @PostMapping("/schedule")
    public ResponseEntity<AppointmentResponseDTO> scheduleAppointment(@Valid @RequestBody AppointmentRequestDTO dto) {
        Appointment appointment = appointmentService.scheduleAppointment(dto);
        return new ResponseEntity<>(convertToResponseDTO(appointment), HttpStatus.CREATED);
    }

    @GetMapping("/{appointmentId}")
    public ResponseEntity<AppointmentResponseDTO> getAppointmentDetails(@PathVariable Long appointmentId) {
        Appointment appointment = appointmentService.getAppointmentById(appointmentId);
        return ResponseEntity.ok(convertToResponseDTO(appointment));
    }

    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<List<AppointmentResponseDTO>> getTutorAppointments(@PathVariable Long tutorId) {
        List<Appointment> appointments = appointmentService.getAppointmentsByTutor(tutorId);
        List<AppointmentResponseDTO> dtos = appointments.stream().map(this::convertToResponseDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{appointmentId}/cancel")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long appointmentId, @Valid @RequestBody CancellationRequestDTO dto) {
        try {
            Appointment appointment = appointmentService.cancelAppointment(appointmentId, dto.getReason());
            return ResponseEntity.ok(convertToResponseDTO(appointment));
        } catch (CancellationNotAllowedException ex) {
            BigDecimal fee = appointmentService.calculateCancellationFee(appointmentId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new CancellationFeeResponse(
                    ex.getMessage(),
                    fee,
                    ex.getMinutesUntilAppointment(),
                    ex.getPenaltyPercent()
            ));
        }
    }

    @GetMapping("/available-slots")
    public ResponseEntity<List<LocalDateTime>> getAvailableSlots(
            @RequestParam LocalDateTime from,
            @RequestParam LocalDateTime to) {
        List<LocalDateTime> slots = availabilityService.generateAvailableSlots(from, to);
        return ResponseEntity.ok(slots);
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

    @lombok.Getter
    @lombok.AllArgsConstructor
    private static class ServiceTypeResponse {
        private String code;
        private String label;
        private BigDecimal price;
        private String category;
    }

    @lombok.Getter
    @lombok.AllArgsConstructor
    private static class CancellationFeeResponse {
        private String message;
        private BigDecimal fee;
        private Long minutesUntilAppointment;
        private Double penaltyPercent;
    }
}
