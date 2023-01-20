package com.minsait.TestingMicroservices.exceptions;

public class DineroInsuficienteException extends RuntimeException {
    public DineroInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
