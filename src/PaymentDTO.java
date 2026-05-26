package com.petshop.domain.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import com.petshop.domain.enum.PaymentMethod;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {

    @NotNull(message = "ID do agendamento é obrigatório")
    private Long appointmentId;

    @NotNull(message = "Forma de pagamento é obrigatória")
    private PaymentMethod paymentMethod;

    @Digits(integer = 10, fraction = 2, message = "Valor inválido")
    @Positive(message = "Valor deve ser positivo")
    private BigDecimal amount;

    @DecimalMin(value = "0.0", message = "Desconto não pode ser negativo")
    private BigDecimal discount = BigDecimal.ZERO;

    private String notes;
}
