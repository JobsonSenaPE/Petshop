package com.petshop.domain.enum;

public enum PaymentMethod {
    CASH("Dinheiro"),
    PIX("PIX"),
    CREDIT_CARD("Cartão de Crédito"),
    DEBIT_CARD("Cartão de Débito");

    private final String label;

    PaymentMethod(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
