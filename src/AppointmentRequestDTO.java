package com.petshop.domain.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import com.petshop.domain.enum.ServiceType;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentRequestDTO {

    @NotNull(message = "ID do animal é obrigatório")
    private Long animalId;

    @NotNull(message = "ID do tutor é obrigatório")
    private Long tutorId;

    @NotNull(message = "Data e hora são obrigatórias")
    @Future(message = "Data e hora devem estar no futuro")
    private LocalDateTime scheduledDateTime;

    @NotNull(message = "Tipo de serviço é obrigatório")
    private ServiceType serviceType;

    private String notes;
}
