package com.retrocore.juan.mod2.login.application.usecase;

import com.retrocore.juan.mod2.domain.exception.CredencialInvalidaException;
import com.retrocore.juan.mod2.domain.ports.PasswordEncryptorPort;
import com.retrocore.juan.mod2.domain.ports.TokenGeneratorPort;
import com.retrocore.juan.mod2.domain.ports.UsuarioRepositoryPort;
import com.retrocore.juan.mod2.login.application.dto.LoginDTO;
import com.retrocore.juan.mod2.login.application.dto.TokenDTO;
import org.springframework.stereotype.Service;

@Service
public class AutenticarUsuarioUseCase {

    private final UsuarioRepositoryPort repository;
    private final PasswordEncryptorPort encryptor;
    private final TokenGeneratorPort tokenService;

    public AutenticarUsuarioUseCase(UsuarioRepositoryPort repository,
                                    PasswordEncryptorPort encryptor,
                                    TokenGeneratorPort tokenService) {
        this.repository = repository;
        this.encryptor = encryptor;
        this.tokenService = tokenService;
    }

    public TokenDTO executar(LoginDTO dto) {
        var user = repository.buscarPorEmail(dto.email())
                .orElseThrow(CredencialInvalidaException::new);

        boolean ok = encryptor.comparar(dto.senha(), user.getSenhaHash());
        if (!ok) throw new CredencialInvalidaException();

        String token = tokenService.gerarToken(user.getEmail());

        return new TokenDTO(token);
    }
}
