package com.retrocore.juan.mod2.login.application.mapper;

import com.retrocore.juan.mod2.domain.model.UsuarioCredencial;
import com.retrocore.juan.mod2.login.application.dto.RegistroDTO;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioCredencial toModel(RegistroDTO dto, String senhaHash) {
        return new UsuarioCredencial(dto.email(), senhaHash);
    }
}
