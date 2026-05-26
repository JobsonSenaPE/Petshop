package com.petshop.domain.enum;

public enum AppointmentStatus {
    SCHEDULED("Agendado"),
    CONFIRMED("Confirmado"),
    IN_PROGRESS("Em andamento"),
    COMPLETED("Concluído"),
    CANCELLED("Cancelado"),
    NO_SHOW("Não compareceu");

    private final String label;

    AppointmentStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public boolean isCancellable() {
        return this == SCHEDULED || this == CONFIRMED;
    }
}
