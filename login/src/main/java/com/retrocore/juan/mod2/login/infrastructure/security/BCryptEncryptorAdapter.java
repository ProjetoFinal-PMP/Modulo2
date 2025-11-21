package com.retrocore.juan.mod2.login.infrastructure.security;

import com.retrocore.juan.mod2.domain.ports.PasswordEncryptorPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptEncryptorAdapter implements PasswordEncryptorPort {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String hash(String senha) {
        return encoder.encode(senha);
    }

    @Override
    public boolean comparar(String senhaPura, String hash) {
        return encoder.matches(senhaPura, hash);
    }
}
