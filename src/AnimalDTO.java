package com.petshop.domain.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import com.petshop.domain.enum.Species;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnimalDTO {
    private Long id;

    @NotBlank(message = "Nome do animal é obrigatório")
    @Size(min = 2, max = 50, message = "Nome deve ter entre 2 e 50 caracteres")
    private String name;

    @NotNull(message = "Espécie é obrigatória")
    private Species species;

    @NotBlank(message = "Raça é obrigatória")
    @Size(min = 2, max = 50, message = "Raça deve ter entre 2 e 50 caracteres")
    private String breed;

    @NotNull(message = "Idade é obrigatória")
    @Min(value = 0, message = "Idade não pode ser negativa")
    @Max(value = 50, message = "Idade máxima deve ser 50 anos")
    private Integer age;

    @Min(value = 0, message = "Peso não pode ser negativo")
    private Double weight;

    private String observations;

    @NotNull(message = "ID do tutor é obrigatório")
    private Long tutorId;
}
