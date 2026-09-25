package com.texflow.backend.dto;

import com.texflow.backend.model.UserType;
import com.texflow.backend.model.Usuario;

public class UsuarioResponse {

    private Long idUsuario;
    private String nome;
    private String email;
    private UserType tipo;

    public UsuarioResponse(Usuario usuario) {
        this.idUsuario = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.tipo = usuario.getTipo();
    }

    public Long getIdUsuario() {
        return idUsuario;
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
