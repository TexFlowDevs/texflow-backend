package com.texflow.backend.dto;

import com.texflow.backend.model.UserType;
import com.texflow.backend.model.Usuario;

/**
 * O que a API devolve sobre um usuario - de proposito SEM o campo senha,
 * nem que seja o hash. Nao ha motivo pro frontend receber isso de volta.
 */
public class UsuarioResponse {

    private Long id;
    private String nome;
    private String email;
    private UserType tipo;

    public UsuarioResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.tipo = usuario.getTipo();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public UserType getTipo() {
        return tipo;
    }
}
