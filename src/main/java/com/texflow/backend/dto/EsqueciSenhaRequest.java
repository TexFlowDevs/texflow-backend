package com.texflow.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class EsqueciSenhaRequest {

    @NotBlank
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
