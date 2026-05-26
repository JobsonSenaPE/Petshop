package com.petshop.controller;

import com.petshop.domain.dto.PaymentDTO;
import com.petshop.domain.entity.ServiceRecord;
import com.petshop.service.PaymentService;
import com.petshop.service.ServiceRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/public/payments")
@RequiredArgsConstructor
public class PaymentPublicController {

    private final PaymentService paymentService;
    private final ServiceRecordService serviceRecordService;

    @PostMapping("/process")
    public ResponseEntity<PaymentResponseDTO> processPayment(@Valid @RequestBody PaymentDTO paymentDTO) {
        ServiceRecord record = paymentService.processPayment(paymentDTO);
        PaymentResponseDTO response = convertToDTO(record);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/tutor/{tutorId}/total-spent")
    public ResponseEntity<TotalSpentDTO> getTotalSpentByTutor(@PathVariable Long tutorId) {
        BigDecimal totalSpent = paymentService.calculateTotalSpentByTutor(tutorId);
        boolean isVIP = paymentService.isTutorVIP(tutorId);
        return ResponseEntity.ok(new TotalSpentDTO(totalSpent, isVIP));
    }

    @GetMapping("/tutor/{tutorId}/history")
    public ResponseEntity<List<ServiceRecordDTO>> getPaymentHistoryByTutor(@PathVariable Long tutorId) {
        List<ServiceRecord> records = serviceRecordService.getServiceHistoryByTutor(tutorId);
        List<ServiceRecordDTO> dtos = records.stream().map(this::convertServiceRecordToDTO).toList();
        return ResponseEntity.ok(dtos);
    }

    private PaymentResponseDTO convertToDTO(ServiceRecord record) {
        return PaymentResponseDTO.builder()
                .id(record.getId())
                .appointmentId(record.getAppointment().getId())
                .tutorName(record.getTutor().getName())
                .animalName(record.getAnimal().getName())
                .serviceType(record.getServiceType().getLabel())
                .paymentMethod(record.getPaymentMethod().getLabel())
                .amountPaid(record.getAmountPaid())
                .discountApplied(record.getDiscountApplied())
                .cancellationFee(record.getCancellationFee())
                .recordedAt(record.getRecordedAt())
                .build();
    }

    private ServiceRecordDTO convertServiceRecordToDTO(ServiceRecord record) {
        return ServiceRecordDTO.builder()
                .id(record.getId())
                .animalName(record.getAnimal().getName())
                .serviceType(record.getServiceType().getLabel())
                .paymentMethod(record.getPaymentMethod().getLabel())
                .amountPaid(record.getAmountPaid())
                .recordedAt(record.getRecordedAt())
                .build();
    }

    @lombok.Getter
    @lombok.AllArgsConstructor
    private static class TotalSpentDTO {
        private BigDecimal totalSpent;
        private Boolean isVIP;
    }

    @lombok.Getter
    @lombok.Builder
    @lombok.AllArgsConstructor
    private static class PaymentResponseDTO {
        private Long id;
        private Long appointmentId;
        private String tutorName;
        private String animalName;
        private String serviceType;
        private String paymentMethod;
        private BigDecimal amountPaid;
        private BigDecimal discountApplied;
        private BigDecimal cancellationFee;
        private java.time.LocalDateTime recordedAt;
    }

    @lombok.Getter
    @lombok.Builder
    @lombok.AllArgsConstructor
    private static class ServiceRecordDTO {
        private Long id;
        private String animalName;
        private String serviceType;
        private String paymentMethod;
        private BigDecimal amountPaid;
        private java.time.LocalDateTime recordedAt;
    }
}
