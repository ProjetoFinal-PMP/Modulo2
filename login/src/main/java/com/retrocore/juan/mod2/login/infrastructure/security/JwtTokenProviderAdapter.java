package com.retrocore.juan.mod2.login.infrastructure.security;

import com.retrocore.juan.mod2.domain.ports.TokenGeneratorPort;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProviderAdapter implements TokenGeneratorPort {

    @Value("${JWT_SECRET}")
    private String secret;

    @Value("${JWT_EXPIRATION_MS}")
    private Long expirationMs;

    @Override
    public String gerarToken(String email) {
        Date agora = new Date();
        Date validade = new Date(agora.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(agora)
                .setExpiration(validade)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
}
