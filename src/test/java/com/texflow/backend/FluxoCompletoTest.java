package com.texflow.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:h2:mem:texflow;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.properties.hibernate.default_schema=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class FluxoCompletoTest {

    @LocalServerPort
    private int porta;

    private final HttpClient http = HttpClient.newHttpClient();
    private final JsonMapper json = JsonMapper.builder().build();

    private HttpResponse<String> enviar(String metodo, String caminho, String corpo) throws Exception {
        HttpRequest.Builder req = HttpRequest.newBuilder(URI.create("http://localhost:" + porta + caminho))
                .header("Content-Type", "application/json");
        if (corpo == null) {
            req.method(metodo, HttpRequest.BodyPublishers.noBody());
        } else {
            req.method(metodo, HttpRequest.BodyPublishers.ofString(corpo));
        }
        return http.send(req.build(), HttpResponse.BodyHandlers.ofString());
    }

    private JsonNode ok(HttpResponse<String> resposta) throws Exception {
        assertEquals(200, resposta.statusCode(), resposta.body());
        return json.readTree(resposta.body());
    }

    @Test
    void pedidoComProcessosEStatus() throws Exception {
        JsonNode cliente = ok(enviar("POST", "/api/empresas",
                "{\"nomeFantasia\":\"Moda João\",\"cnpj\":\"12.345.678/0001-90\","
                        + "\"contato\":{\"nome\":\"Ana\",\"telefone\":\"(47) 99999-0001\"}}"));
        JsonNode oficina = ok(enviar("POST", "/api/empresas",
                "{\"nomeFantasia\":\"Cortex Ltda.\",\"cnpj\":\"98.765.432/0001-10\","
                        + "\"contato\":{\"nome\":\"Bia\",\"telefone\":\"(47) 99999-0002\"}}"));
        long clienteId = cliente.get("id").asLong();
        long oficinaId = oficina.get("id").asLong();
        assertEquals("Ana", cliente.get("contato").get("nome").asText());

        JsonNode pedido = ok(enviar("POST", "/api/operacoes",
                "{\"cliente\":{\"id\":" + clienteId + "},\"dataEntrega\":\"2026-11-10\","
                        + "\"status\":\"naoIniciado\","
                        + "\"gradePedido\":[{\"descricao\":\"P\",\"quantidade\":10},{\"descricao\":\"M\",\"quantidade\":20}],"
                        + "\"processos\":["
                        + "{\"descricao\":\"Corte\",\"status\":\"naoIniciado\",\"empresaResponsavel\":{\"id\":" + oficinaId + "}},"
                        + "{\"descricao\":\"Costura\",\"status\":\"naoIniciado\"}]}"));
        assertEquals(2, pedido.get("processos").size());

        JsonNode lista = ok(enviar("GET", "/api/operacoes", null));
        JsonNode salvo = lista.get(0);
        assertEquals("Moda João", salvo.get("cliente").get("nomeFantasia").asText());
        assertEquals(2, salvo.get("gradePedido").size());
        assertEquals("Cortex Ltda.", salvo.get("processos").get(0).get("empresaResponsavel").get("nomeFantasia").asText());

        long corteId = salvo.get("processos").get(0).get("id").asLong();
        long costuraId = salvo.get("processos").get(1).get("id").asLong();

        JsonNode aposPrimeiro = ok(enviar("PUT", "/api/processos/" + corteId + "/status",
                "{\"status\":\"concluido\"}"));
        assertEquals("emAndamento", aposPrimeiro.get("status").asText());

        JsonNode aposSegundo = ok(enviar("PUT", "/api/processos/" + costuraId + "/status",
                "{\"status\":\"concluido\"}"));
        assertEquals("concluido", aposSegundo.get("status").asText());

        JsonNode reaberto = ok(enviar("PUT", "/api/processos/" + corteId + "/status",
                "{\"status\":\"naoIniciado\"}"));
        assertEquals("emAndamento", reaberto.get("status").asText());

        ok(enviar("PUT", "/api/processos/" + costuraId + "/status", "{\"status\":\"naoIniciado\"}"));
        JsonNode fim = ok(enviar("GET", "/api/operacoes/" + salvo.get("id").asLong(), null));
        assertEquals("naoIniciado", fim.get("status").asText());
    }

    @Test
    void usuarioSenhaEPerfil() throws Exception {
        JsonNode criado = ok(enviar("POST", "/api/usuarios",
                "{\"nome\":\"Maria\",\"email\":\"maria@texflow.com\",\"senha\":\"Abc123!\",\"tipo\":\"GESTOR\"}"));
        long id = criado.get("idUsuario").asLong();
        assertEquals("GESTOR", criado.get("tipo").asText());

        HttpResponse<String> duplicado = enviar("POST", "/api/usuarios",
                "{\"nome\":\"Maria\",\"email\":\"maria@texflow.com\",\"senha\":\"Abc123!\",\"tipo\":\"OPERADOR\"}");
        assertEquals(409, duplicado.statusCode());
        assertTrue(duplicado.body().contains("Ja existe"), duplicado.body());

        HttpResponse<String> errada = enviar("PUT", "/api/usuarios/" + id + "/senha",
                "{\"senhaAtual\":\"errada\",\"novaSenha\":\"Nova123!\"}");
        assertEquals(400, errada.statusCode());
        assertTrue(errada.body().contains("incorreta"), errada.body());

        ok(enviar("PUT", "/api/usuarios/" + id + "/senha",
                "{\"senhaAtual\":\"Abc123!\",\"novaSenha\":\"Nova123!\"}"));

        ok(enviar("POST", "/api/auth/login", "{\"email\":\"maria@texflow.com\",\"senha\":\"Nova123!\"}"));
        assertEquals(401, enviar("POST", "/api/auth/login",
                "{\"email\":\"maria@texflow.com\",\"senha\":\"Abc123!\"}").statusCode());

        String listagem = enviar("GET", "/api/usuarios", null).body();
        assertFalse(listagem.contains("senha"), listagem);
    }
}
