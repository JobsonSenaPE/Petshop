package com.petshop.service;

import com.petshop.domain.dto.AppointmentRequestDTO;
import com.petshop.domain.dto.AppointmentResponseDTO;
import com.petshop.domain.entity.Appointment;
import com.petshop.domain.entity.Animal;
import com.petshop.domain.entity.Tutor;
import com.petshop.domain.enum.AppointmentStatus;
import com.petshop.exception.EntityNotFoundException;
import com.petshop.exception.CancellationNotAllowedException;
import com.petshop.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentAvailabilityService availabilityService;
    private final AnimalService animalService;
    private final TutorService tutorService;

    private static final long CANCELLATION_NOTICE_MINUTES = 120;
    private static final BigDecimal CANCELLATION_FEE_PERCENT = new BigDecimal("50.00");

    public Appointment scheduleAppointment(AppointmentRequestDTO dto) {
        availabilityService.validateAppointmentDateTime(dto.getScheduledDateTime(), dto.getAnimalId());

        Animal animal = animalService.getAnimalById(dto.getAnimalId());
        Tutor tutor = tutorService.getTutorById(dto.getTutorId());

        Appointment appointment = Appointment.builder()
                .animal(animal)
                .tutor(tutor)
                .scheduledDateTime(dto.getScheduledDateTime())
                .serviceType(dto.getServiceType())
                .status(AppointmentStatus.SCHEDULED)
                .price(dto.getServiceType().getPrice())
                .notes(dto.getNotes())
                .cancellationFeeApplied(false)
                .build();

        return appointmentRepository.save(appointment);
    }

    @Transactional(readOnly = true)
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> EntityNotFoundException.appointmentNotFound(id));
    }

    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByTutor(Long tutorId) {
        tutorService.getTutorById(tutorId);
        return appointmentRepository.findByTutorIdOrderByScheduledDateTimeDesc(tutorId);
    }

    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByAnimal(Long animalId) {
        animalService.getAnimalById(animalId);
        List<AppointmentStatus> activeStatuses = List.of(
                AppointmentStatus.SCHEDULED,
                AppointmentStatus.CONFIRMED,
                AppointmentStatus.IN_PROGRESS
        );
        return appointmentRepository.findByAnimalIdAndStatusIn(animalId, activeStatuses);
    }

    public Appointment confirmAppointment(Long appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        return appointmentRepository.save(appointment);
    }

    public Appointment completeAppointment(Long appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        return appointmentRepository.save(appointment);
    }

    public Appointment cancelAppointment(Long appointmentId, String reason) {
        Appointment appointment = getAppointmentById(appointmentId);

        if (!appointment.getStatus().isCancellable()) {
            throw new IllegalStateException(
                    "Agendamento com status " + appointment.getStatus().getLabel() + " não pode ser cancelado."
            );
        }

        long minutesUntilAppointment = ChronoUnit.MINUTES.between(LocalDateTime.now(), appointment.getScheduledDateTime());

        if (minutesUntilAppointment < CANCELLATION_NOTICE_MINUTES) {
            appointment.setCancellationFeeApplied(true);
            throw CancellationNotAllowedException.withinTwoHours(appointmentId, minutesUntilAppointment);
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancelledAt(LocalDateTime.now());
        appointment.setCancellationReason(reason);
        appointment.setCancellationFeeApplied(false);

        return appointmentRepository.save(appointment);
    }

    public BigDecimal calculateCancellationFee(Long appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        long minutesUntilAppointment = ChronoUnit.MINUTES.between(LocalDateTime.now(), appointment.getScheduledDateTime());

        if (minutesUntilAppointment < CANCELLATION_NOTICE_MINUTES) {
            return appointment.getPrice().multiply(CANCELLATION_FEE_PERCENT).divide(new BigDecimal("100"));
        }

        return BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getTodayAppointmentsByDepartment(String department) {
        return appointmentRepository.findAll().stream()
                .filter(a -> a.getScheduledDateTime().toLocalDate().equals(LocalDateTime.now().toLocalDate()))
                .filter(a -> a.getServiceType().getDepartment().equals(department))
                .filter(a -> List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED, AppointmentStatus.IN_PROGRESS).contains(a.getStatus()))
                .sorted((a1, a2) -> a1.getScheduledDateTime().compareTo(a2.getScheduledDateTime()))
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getAllAppointmentsDTO() {
        return appointmentRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
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
