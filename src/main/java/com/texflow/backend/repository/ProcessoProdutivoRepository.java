package com.texflow.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.texflow.backend.model.ProcessoProdutivo;

public interface ProcessoProdutivoRepository extends JpaRepository<ProcessoProdutivo, Long> {
}
