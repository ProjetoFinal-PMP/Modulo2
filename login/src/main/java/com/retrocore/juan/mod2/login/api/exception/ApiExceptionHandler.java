package com.retrocore.juan.mod2.login.api.exception;

import com.retrocore.juan.mod2.domain.exception.CredencialInvalidaException;
import com.retrocore.juan.mod2.domain.exception.UsuarioExistenteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(UsuarioExistenteException.class)
    public ResponseEntity<String> usuarioJaExiste(UsuarioExistenteException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(CredencialInvalidaException.class)
    public ResponseEntity<String> credenciaisInvalidas(CredencialInvalidaException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }
}
