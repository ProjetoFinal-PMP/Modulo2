package com.retrocore.juan.mod2.domain.ports;

public interface PasswordEncryptorPort {

    String hash(String senha);

    boolean comparar(String senhaPura, String hash);
}
