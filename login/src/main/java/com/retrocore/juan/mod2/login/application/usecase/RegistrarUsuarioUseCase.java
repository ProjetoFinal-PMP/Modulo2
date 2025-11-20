package com.retrocore.juan.mod2.login.application.usecase;

import com.retrocore.juan.mod2.domain.exception.UsuarioExistenteException;
import com.retrocore.juan.mod2.domain.model.UsuarioCredencial;
import com.retrocore.juan.mod2.domain.ports.PasswordEncryptorPort;
import com.retrocore.juan.mod2.domain.ports.UsuarioRepositoryPort;
import com.retrocore.juan.mod2.login.application.dto.RegistroDTO;
import com.retrocore.juan.mod2.login.application.mapper.UsuarioMapper;
import org.springframework.stereotype.Service;

@Service
public class RegistrarUsuarioUseCase {

    private final UsuarioRepositoryPort repository;
    private final PasswordEncryptorPort encryptor;
    private final UsuarioMapper mapper;

    public RegistrarUsuarioUseCase(UsuarioRepositoryPort repository,
                                   PasswordEncryptorPort encryptor,
                                   UsuarioMapper mapper) {
        this.repository = repository;
        this.encryptor = encryptor;
        this.mapper = mapper;
    }

    public void executar(RegistroDTO dto) {
        repository.buscarPorEmail(dto.email())
                .ifPresent(u -> { throw new UsuarioExistenteException(dto.email()); });

        String hash = encryptor.hash(dto.senha());

        UsuarioCredencial model = mapper.toModel(dto, hash);

        repository.salvar(model);
    }
}
