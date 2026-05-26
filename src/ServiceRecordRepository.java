package com.petshop.repository;

import com.petshop.domain.entity.ServiceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ServiceRecordRepository extends JpaRepository<ServiceRecord, Long> {
    List<ServiceRecord> findByAnimalIdOrderByRecordedAtDesc(Long animalId);
    
    List<ServiceRecord> findByTutorIdOrderByRecordedAtDesc(Long tutorId);
    
    @Query("SELECT sr FROM ServiceRecord sr WHERE sr.tutor.id = ?1 AND sr.recordedAt >= ?2 ORDER BY sr.recordedAt DESC")
    List<ServiceRecord> findServiceHistoryByTutor(Long tutorId, LocalDateTime since);
    
    @Query("SELECT COUNT(sr) FROM ServiceRecord sr WHERE sr.tutor.id = ?1")
    int countAppointmentsByTutor(Long tutorId);
}
