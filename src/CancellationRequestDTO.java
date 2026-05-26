package com.petshop.domain.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancellationRequestDTO {

    @NotNull(message = "ID do agendamento é obrigatório")
    private Long appointmentId;

    @NotBlank(message = "Motivo do cancelamento é obrigatório")
    @Size(min = 5, max = 500, message = "Motivo deve ter entre 5 e 500 caracteres")
    private String reason;
}
