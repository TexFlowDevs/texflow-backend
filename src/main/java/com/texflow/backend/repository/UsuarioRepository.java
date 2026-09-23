package com.texflow.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.texflow.backend.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
