package com.petshop.service;

import com.petshop.domain.entity.ServiceRecord;
import com.petshop.exception.EntityNotFoundException;
import com.petshop.repository.ServiceRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServiceRecordService {

    private final ServiceRecordRepository serviceRecordRepository;

    public ServiceRecord getServiceRecord(Long id) {
        return serviceRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Registro de serviço não encontrado com ID: " + id));
    }

    public List<ServiceRecord> getServiceHistoryByAnimal(Long animalId) {
        return serviceRecordRepository.findByAnimalIdOrderByRecordedAtDesc(animalId);
    }

    public List<ServiceRecord> getServiceHistoryByTutor(Long tutorId) {
        return serviceRecordRepository.findByTutorIdOrderByRecordedAtDesc(tutorId);
    }

    public List<ServiceRecord> getServiceHistoryByTutorSince(Long tutorId, LocalDateTime since) {
        return serviceRecordRepository.findServiceHistoryByTutor(tutorId, since);
    }

    public List<ServiceRecord> getServiceHistoryLastYear(Long tutorId) {
        LocalDateTime oneYearAgo = LocalDateTime.now().minus(365, ChronoUnit.DAYS);
        return getServiceHistoryByTutorSince(tutorId, oneYearAgo);
    }

    public int getTotalAppointmentCountByTutor(Long tutorId) {
        return serviceRecordRepository.countAppointmentsByTutor(tutorId);
    }

    public List<ServiceRecord> getAllServiceRecords() {
        return serviceRecordRepository.findAll();
    }
}
