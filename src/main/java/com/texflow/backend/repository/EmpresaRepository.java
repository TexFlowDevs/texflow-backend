package com.texflow.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.texflow.backend.model.Empresa;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
}
