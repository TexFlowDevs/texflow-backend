package com.texflow.backend.model;

import jakarta.persistence.Embeddable;

/**
 * Sem identidade propria (sem id) - sempre vive dentro de outra entidade,
 * por isso e' @Embeddable em vez de @Entity.
 */
@Embeddable
public class Contato {

    private String nome;
    private String telefone;

    public Contato() {
    }

    public Contato(String nome, String telefone) {
        this.nome = nome;
        this.telefone = telefone;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
