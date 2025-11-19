package com.retrocore.juan.mod2.domain.exception;

public class UsuarioExistenteException extends RuntimeException {

    public UsuarioExistenteException(String email) {
        super("Já existe um usuário cadastrado com o e-mail: " + email);
    }
}
