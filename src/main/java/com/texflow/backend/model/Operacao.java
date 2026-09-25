package com.texflow.backend.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Operacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "referencia_id")
    private Referencia referencia;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Empresa cliente;

    private LocalDate dataEntrega;

    @ElementCollection
    @CollectionTable(name = "operacao_grade_pedido", joinColumns = @JoinColumn(name = "operacao_id"))
    private List<ItemGrade> gradePedido = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "operacao_grade_fabricada", joinColumns = @JoinColumn(name = "operacao_id"))
    private List<ItemGrade> gradeFabricada = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "operacao_id")
    private List<ProcessoProdutivo> processos = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Status status;

    public Operacao() {
    }

    public Long getId() {
        return id;
    }

    public Referencia getReferencia() {
        return referencia;
    }

    public void setReferencia(Referencia referencia) {
        this.referencia = referencia;
    }

    public Empresa getCliente() {
        return cliente;
    }

    public void setCliente(Empresa cliente) {
        this.cliente = cliente;
    }

    public LocalDate getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(LocalDate dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    public List<ItemGrade> getGradePedido() {
        return gradePedido;
    }

    public List<ItemGrade> getGradeFabricada() {
        return gradeFabricada;
    }

    public List<ProcessoProdutivo> getProcessos() {
        return processos;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
