package com.petshop.domain.dto;

import lombok.*;
import com.petshop.domain.enum.ServiceType;
import com.petshop.domain.enum.AppointmentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponseDTO {
    private Long id;
    private Long animalId;
    private String animalName;
    private Long tutorId;
    private String tutorName;
    private LocalDateTime scheduledDateTime;
    private ServiceType serviceType;
    private AppointmentStatus status;
    private BigDecimal price;
    private String notes;
    private LocalDateTime createdAt;
}
