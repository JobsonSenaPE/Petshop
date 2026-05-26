package com.petshop.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.petshop.domain.enum.ServiceType;
import com.petshop.domain.enum.AppointmentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments", uniqueConstraints = {
    @UniqueConstraint(name = "uc_animal_datetime", columnNames = {"animal_id", "scheduled_at"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", nullable = false)
    private Tutor tutor;

    @NotNull(message = "Data e hora são obrigatórias")
    @Column(nullable = false, name = "scheduled_at")
    private LocalDateTime scheduledDateTime;

    @NotNull(message = "Tipo de serviço é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType serviceType;

    @NotNull(message = "Status é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    @Digits(integer = 10, fraction = 2, message = "Preço inválido")
    @Positive(message = "Preço deve ser positivo")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(length = 500)
    private String notes;

    private Boolean cancellationFeeApplied = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime cancelledAt;

    @Column(length = 255)
    private String cancellationReason;

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", animal='" + animal.getName() + '\'' +
                ", scheduledDateTime=" + scheduledDateTime +
                ", serviceType=" + serviceType +
                ", status=" + status +
                '}';
    }
}
