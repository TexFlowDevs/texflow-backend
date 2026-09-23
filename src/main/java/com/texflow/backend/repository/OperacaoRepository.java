package com.texflow.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.texflow.backend.model.Operacao;

public interface OperacaoRepository extends JpaRepository<Operacao, Long> {
}
