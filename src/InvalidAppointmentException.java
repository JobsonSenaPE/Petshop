package com.petshop.exception;

import java.time.LocalDateTime;

public class InvalidAppointmentException extends RuntimeException {
    private final LocalDateTime conflictingDateTime;

    public InvalidAppointmentException(String message) {
        super(message);
        this.conflictingDateTime = null;
    }

    public InvalidAppointmentException(String message, LocalDateTime conflictingDateTime) {
        super(message);
        this.conflictingDateTime = conflictingDateTime;
    }

    public LocalDateTime getConflictingDateTime() {
        return conflictingDateTime;
    }

    public static InvalidAppointmentException slotNotAvailable(LocalDateTime dateTime) {
        return new InvalidAppointmentException(
            "Horário não disponível: " + dateTime + ". Por favor, selecione outro horário.",
            dateTime
        );
    }

    public static InvalidAppointmentException businessHourViolation() {
        return new InvalidAppointmentException(
            "Agendamentos só são permitidos entre 08:00 e 20:00, com intervalo de almoço entre 12:00 e 13:00."
        );
    }

    public static InvalidAppointmentException twoHourIntervalViolation(LocalDateTime dateTime) {
        return new InvalidAppointmentException(
            "Deve haver um intervalo de 2 horas entre os agendamentos. Próximo horário disponível: " + dateTime,
            dateTime
        );
    }
}
