package com.petshop.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public static EntityNotFoundException tutorNotFound(Long id) {
        return new EntityNotFoundException("Tutor não encontrado com ID: " + id);
    }

    public static EntityNotFoundException animalNotFound(Long id) {
        return new EntityNotFoundException("Animal não encontrado com ID: " + id);
    }

    public static EntityNotFoundException appointmentNotFound(Long id) {
        return new EntityNotFoundException("Agendamento não encontrado com ID: " + id);
    }
}
