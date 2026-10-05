package com.texflow.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.texflow.backend.model.Operacao;

public interface OperacaoRepository extends JpaRepository<Operacao, Long> {

    Optional<Operacao> findByProcessosId(Long processoId);
}
