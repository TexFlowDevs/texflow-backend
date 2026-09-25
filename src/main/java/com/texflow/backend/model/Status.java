package com.texflow.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Status {
    @JsonProperty("naoIniciado")
    NAO_INICIADO,

    @JsonProperty("emAndamento")
    EM_ANDAMENTO,

    @JsonProperty("concluido")
    CONCLUIDO,

    @JsonProperty("cancelado")
    CANCELADO
}
