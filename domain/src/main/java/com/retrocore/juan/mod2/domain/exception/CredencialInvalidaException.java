package com.retrocore.juan.mod2.domain.exception;

public class CredencialInvalidaException extends RuntimeException {
    
    public CredencialInvalidaException() {
        super("E-mail ou senha inválidos.");
    }
}
