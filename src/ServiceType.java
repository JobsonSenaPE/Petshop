package com.petshop.domain.enum;

import java.math.BigDecimal;

public enum ServiceType {
    BATH("Banho", new BigDecimal("45.00")),
    GROOMING("Tosa", new BigDecimal("60.00")),
    VETERINARY("Consulta Veterinária", new BigDecimal("90.00")),
    BATH_GROOMING("Combo: Banho + Tosa", new BigDecimal("95.00")),
    BATH_VETERINARY("Combo: Banho + Consulta", new BigDecimal("125.00")),
    GROOMING_VETERINARY("Combo: Tosa + Consulta", new BigDecimal("140.00")),
    FULL_SERVICE("Combo: Banho + Tosa + Consulta", new BigDecimal("180.00"));

    private final String label;
    private final BigDecimal price;

    ServiceType(String label, BigDecimal price) {
        this.label = label;
        this.price = price;
    }

    public String getLabel() {
        return label;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCategory() {
        return switch (this) {
            case BATH, GROOMING, VETERINARY -> "INDIVIDUAL";
            default -> "COMBO";
        };
    }

    public String getDepartment() {
        return switch (this) {
            case BATH, BATH_GROOMING, BATH_VETERINARY, FULL_SERVICE -> "BANHO";
            case GROOMING, GROOMING_VETERINARY -> "TOSA";
            case VETERINARY -> "VETERINÁRIO";
        };
    }
}
