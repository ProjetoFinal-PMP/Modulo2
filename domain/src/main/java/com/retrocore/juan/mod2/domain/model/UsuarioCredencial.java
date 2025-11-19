package com.retrocore.juan.mod2.domain.model;

public class UsuarioCredencial {

    private Long id;
    private String email;
    private String senhaHash;

    public UsuarioCredencial(Long id, String email, String senhaHash) {
        this.id = id;
        this.email = email;
        this.senhaHash = senhaHash;
    }

    public UsuarioCredencial(String email, String senhaHash) {
        this(null, email, senhaHash);
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
