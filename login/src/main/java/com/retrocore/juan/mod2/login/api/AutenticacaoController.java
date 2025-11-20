package com.retrocore.juan.mod2.login.api;

import com.retrocore.juan.mod2.login.application.dto.LoginDTO;
import com.retrocore.juan.mod2.login.application.dto.RegistroDTO;
import com.retrocore.juan.mod2.login.application.dto.TokenDTO;
import com.retrocore.juan.mod2.login.application.usecase.AutenticarUsuarioUseCase;
import com.retrocore.juan.mod2.login.application.usecase.RegistrarUsuarioUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AutenticacaoController {

    private final RegistrarUsuarioUseCase registrar;
    private final AutenticarUsuarioUseCase autenticar;

    public AutenticacaoController(RegistrarUsuarioUseCase registrar,
                                  AutenticarUsuarioUseCase autenticar) {
        this.registrar = registrar;
        this.autenticar = autenticar;
    }

    @PostMapping("/registrar")
    public ResponseEntity<Void> registrar(@RequestBody RegistroDTO dto) {
        registrar.executar(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO dto) {
        TokenDTO token = autenticar.executar(dto);
        return ResponseEntity.ok(token);
    }
}

