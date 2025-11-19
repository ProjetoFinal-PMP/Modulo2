package com.retrocore.juan.mod2.domain.ports;

import com.retrocore.juan.mod2.domain.model.UsuarioCredencial;

import java.util.Optional;

public interface UsuarioRepositoryPort {

    UsuarioCredencial salvar(UsuarioCredencial usuario);

    Optional<UsuarioCredencial> buscarPorEmail(String email);
}
