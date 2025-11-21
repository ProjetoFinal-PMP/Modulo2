package com.retrocore.juan.mod2.login.infrastructure.adapter;

import com.retrocore.juan.mod2.domain.model.UsuarioCredencial;
import com.retrocore.juan.mod2.domain.ports.UsuarioRepositoryPort;
import com.retrocore.juan.mod2.login.infrastructure.jpa.UsuarioEntity;
import com.retrocore.juan.mod2.login.infrastructure.jpa.UsuarioJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository repo;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public UsuarioCredencial salvar(UsuarioCredencial u) {
        UsuarioEntity entity = new UsuarioEntity(
                u.getId(),
                u.getEmail(),
                u.getSenhaHash()
        );
        UsuarioEntity salvo = repo.save(entity);
        u.setId(salvo.getId());
        return u;
    }

    @Override
    public Optional<UsuarioCredencial> buscarPorEmail(String email) {
        return repo.findByEmail(email)
                .map(e -> new UsuarioCredencial(e.getId(), e.getEmail(), e.getSenhaHash()));
    }
}
