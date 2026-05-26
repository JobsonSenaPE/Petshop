package com.petshop.service;

import com.petshop.domain.entity.Appointment;
import com.petshop.domain.enum.AppointmentStatus;
import com.petshop.exception.InvalidAppointmentException;
import com.petshop.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppointmentAvailabilityService {

    private final AppointmentRepository appointmentRepository;

    private static final int BUSINESS_START_HOUR = 8;
    private static final int BUSINESS_END_HOUR = 20;
    private static final int LUNCH_START_HOUR = 12;
    private static final int LUNCH_END_HOUR = 13;
    private static final int APPOINTMENT_INTERVAL_HOURS = 2;

    public void validateAppointmentDateTime(LocalDateTime dateTime, Long animalId) {
        validateBusinessHours(dateTime);
        validateLunchBreak(dateTime);
        validateIntervalBetweenAppointments(dateTime, animalId);
    }

    private void validateBusinessHours(LocalDateTime dateTime) {
        LocalTime time = dateTime.toLocalTime();
        if (time.getHour() < BUSINESS_START_HOUR || time.getHour() >= BUSINESS_END_HOUR) {
            throw InvalidAppointmentException.businessHourViolation();
        }
    }

    private void validateLunchBreak(LocalDateTime dateTime) {
        LocalTime time = dateTime.toLocalTime();
        if (time.getHour() >= LUNCH_START_HOUR && time.getHour() < LUNCH_END_HOUR) {
            throw InvalidAppointmentException.businessHourViolation();
        }
    }

    private void validateIntervalBetweenAppointments(LocalDateTime dateTime, Long animalId) {
        LocalDateTime twoHoursBefore = dateTime.minusHours(APPOINTMENT_INTERVAL_HOURS);
        LocalDateTime twoHoursAfter = dateTime.plusHours(APPOINTMENT_INTERVAL_HOURS);

        List<Appointment> conflictingAppointments = appointmentRepository
                .findByAnimalIdAndStatusIn(
                        animalId,
                        List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED, AppointmentStatus.IN_PROGRESS)
                );

        for (Appointment apt : conflictingAppointments) {
            LocalDateTime aptTime = apt.getScheduledDateTime();
            if (aptTime.isAfter(twoHoursBefore) && aptTime.isBefore(twoHoursAfter)) {
                throw InvalidAppointmentException.twoHourIntervalViolation(dateTime);
            }
        }
    }

    public List<Appointment> getAvailableSlots(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return appointmentRepository.findAvailableSlots(startDateTime, endDateTime);
    }

    public List<LocalDateTime> generateAvailableSlots(LocalDateTime from, LocalDateTime to) {
        List<LocalDateTime> slots = new java.util.ArrayList<>();
        LocalDateTime current = from.withHour(BUSINESS_START_HOUR).withMinute(0).withSecond(0);

        while (current.isBefore(to)) {
            if (!isLunchTime(current) && current.isBefore(current.withHour(BUSINESS_END_HOUR))) {
                slots.add(current);
            }
            current = current.plusHours(APPOINTMENT_INTERVAL_HOURS);
        }

        return slots;
    }

    private boolean isLunchTime(LocalDateTime dateTime) {
        LocalTime time = dateTime.toLocalTime();
        return time.getHour() >= LUNCH_START_HOUR && time.getHour() < LUNCH_END_HOUR;
    }
}
