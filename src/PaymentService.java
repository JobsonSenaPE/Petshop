package com.petshop.service;

import com.petshop.domain.dto.PaymentDTO;
import com.petshop.domain.entity.Appointment;
import com.petshop.domain.entity.ServiceRecord;
import com.petshop.domain.enum.AppointmentStatus;
import com.petshop.repository.ServiceRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final ServiceRecordRepository serviceRecordRepository;
    private final AppointmentService appointmentService;

    public ServiceRecord processPayment(PaymentDTO paymentDTO) {
        Appointment appointment = appointmentService.getAppointmentById(paymentDTO.getAppointmentId());

        BigDecimal finalAmount = paymentDTO.getAmount();
        if (paymentDTO.getDiscount() != null) {
            finalAmount = finalAmount.subtract(paymentDTO.getDiscount());
        }

        BigDecimal cancellationFee = BigDecimal.ZERO;
        if (appointment.getCancellationFeeApplied()) {
            cancellationFee = appointmentService.calculateCancellationFee(appointment.getId());
            finalAmount = cancellationFee;
        }

        ServiceRecord record = ServiceRecord.builder()
                .appointment(appointment)
                .animal(appointment.getAnimal())
                .tutor(appointment.getTutor())
                .serviceType(appointment.getServiceType())
                .paymentMethod(paymentDTO.getPaymentMethod())
                .amountPaid(finalAmount)
                .discountApplied(paymentDTO.getDiscount())
                .cancellationFee(cancellationFee)
                .notes(paymentDTO.getNotes())
                .build();

        ServiceRecord saved = serviceRecordRepository.save(record);

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentService.completeAppointment(appointment.getId());

        return saved;
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateTotalSpentByTutor(Long tutorId) {
        int appointmentCount = serviceRecordRepository.countAppointmentsByTutor(tutorId);
        return serviceRecordRepository.findByTutorIdOrderByRecordedAtDesc(tutorId).stream()
                .map(ServiceRecord::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public boolean isTutorVIP(Long tutorId) {
        BigDecimal totalSpent = calculateTotalSpentByTutor(tutorId);
        BigDecimal vipThreshold = new BigDecimal("1000.00");
        return totalSpent.compareTo(vipThreshold) >= 0;
    }
}
