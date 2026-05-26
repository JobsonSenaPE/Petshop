package com.petshop.repository;

import com.petshop.domain.entity.Appointment;
import com.petshop.domain.enum.AppointmentStatus;
import com.petshop.domain.enum.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByAnimalIdAndStatusIn(Long animalId, List<AppointmentStatus> statuses);
    
    List<Appointment> findByTutorIdOrderByScheduledDateTimeDesc(Long tutorId);
    
    @Query("SELECT a FROM Appointment a WHERE " +
           "a.scheduledDateTime >= ?1 AND a.scheduledDateTime <= ?2 " +
           "AND a.status IN ('SCHEDULED', 'CONFIRMED') " +
           "ORDER BY a.scheduledDateTime ASC")
    List<Appointment> findAvailableSlots(LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    @Query("SELECT a FROM Appointment a WHERE " +
           "a.serviceType = ?1 AND " +
           "DATE(a.scheduledDateTime) = CURRENT_DATE AND " +
           "a.status IN ('SCHEDULED', 'CONFIRMED', 'IN_PROGRESS')")
    List<Appointment> findDayAppointmentsByServiceType(ServiceType serviceType);
    
    Optional<Appointment> findByAnimalIdAndScheduledDateTime(Long animalId, LocalDateTime scheduledDateTime);
}
