package com.retrocore.juan.mod2.login.infrastructure.jpa;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Column(name = "senha_hash")
    private String senhaHash;

    public UsuarioEntity() {}

    public UsuarioEntity(Long id, String email, String senhaHash) {
        this.id = id;
        this.email = email;
        this.senhaHash = senhaHash;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getSenhaHash() { return senhaHash; }
}
