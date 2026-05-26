package com.petshop.domain.enum;

public enum Species {
    DOG("Cão"),
    CAT("Gato"),
    BIRD("Ave"),
    RABBIT("Coelho"),
    HAMSTER("Hamster"),
    OTHER("Outro");

    private final String label;

    Species(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
