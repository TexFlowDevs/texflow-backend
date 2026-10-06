package com.texflow.backend.dto;

import com.texflow.backend.model.Status;

import jakarta.validation.constraints.NotNull;

public class StatusProcessoRequest {

    @NotNull
    private Status status;

    private Long usuarioId;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}
