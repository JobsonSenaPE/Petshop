package com.petshop.exception;

public class CancellationNotAllowedException extends RuntimeException {
    private final Long appointmentId;
    private final Long minutesUntilAppointment;
    private final Double penaltyPercent;

    public CancellationNotAllowedException(String message, Long appointmentId, Long minutesUntilAppointment, Double penaltyPercent) {
        super(message);
        this.appointmentId = appointmentId;
        this.minutesUntilAppointment = minutesUntilAppointment;
        this.penaltyPercent = penaltyPercent;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public Long getMinutesUntilAppointment() {
        return minutesUntilAppointment;
    }

    public Double getPenaltyPercent() {
        return penaltyPercent;
    }

    public static CancellationNotAllowedException withinTwoHours(Long appointmentId, Long minutes) {
        return new CancellationNotAllowedException(
            String.format("Cancelamento dentro de 2 horas (resta %d minutos). Será cobrado 50%% do valor.",minutes),
            appointmentId,
            minutes,
            50.0
        );
    }
}
