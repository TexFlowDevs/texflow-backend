package com.texflow.backend.model;

import jakarta.persistence.Embeddable;

/**
 * Um item de uma grade de pedido/fabricacao. Ex: "Tamanho P" - 10 pecas.
 * Sem identidade propria - sempre vive dentro da lista de uma Operacao.
 */
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
