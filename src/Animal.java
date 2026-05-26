package com.petshop.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.petshop.domain.enum.Species;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "animals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome do animal é obrigatório")
    @Size(min = 2, max = 50, message = "Nome deve ter entre 2 e 50 caracteres")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Espécie é obrigatória")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Species species;

    @NotBlank(message = "Raça é obrigatória")
    @Size(min = 2, max = 50, message = "Raça deve ter entre 2 e 50 caracteres")
    @Column(nullable = false)
    private String breed;

    @NotNull(message = "Idade é obrigatória")
    @Min(value = 0, message = "Idade não pode ser negativa")
    @Max(value = 50, message = "Idade máxima deve ser 50 anos")
    @Column(nullable = false)
    private Integer age;

    @Min(value = 0, message = "Peso não pode ser negativo")
    @Column(name = "weight_kg")
    private Double weight;

    @Column(length = 500)
    private String observations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", nullable = false)
    private Tutor tutor;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments;

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceRecord> serviceRecords;

    @Override
    public String toString() {
        return "Animal{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", species=" + species +
                ", breed='" + breed + '\'' +
                ", age=" + age +
                '}';
    }
}
