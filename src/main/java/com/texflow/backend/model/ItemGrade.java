package com.texflow.backend.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class ItemGrade {

    private String descricao;
    private int quantidade;

    public ItemGrade() {
    }

    public ItemGrade(String descricao, int quantidade) {
        this.descricao = descricao;
        this.quantidade = quantidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}
