package com.texflow.backend.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.texflow.backend.dto.StatusProcessoRequest;
import com.texflow.backend.model.Operacao;
import com.texflow.backend.model.ProcessoProdutivo;
import com.texflow.backend.model.Status;
import com.texflow.backend.repository.OperacaoRepository;
import com.texflow.backend.repository.ProcessoProdutivoRepository;
import com.texflow.backend.repository.UsuarioRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/processos")
public class ProcessoProdutivoController {

    private final ProcessoProdutivoRepository processoProdutivoRepository;
    private final OperacaoRepository operacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public ProcessoProdutivoController(ProcessoProdutivoRepository processoProdutivoRepository,
            OperacaoRepository operacaoRepository, UsuarioRepository usuarioRepository) {
        this.processoProdutivoRepository = processoProdutivoRepository;
        this.operacaoRepository = operacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public List<ProcessoProdutivo> listar() {
        return processoProdutivoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ProcessoProdutivo buscar(@PathVariable Long id) {
        return buscarEntidade(id);
    }

    @PostMapping
    public ProcessoProdutivo criar(@RequestBody ProcessoProdutivo processo) {
        return processoProdutivoRepository.save(processo);
    }

    @PutMapping("/{id}")
    public ProcessoProdutivo atualizar(@PathVariable Long id, @RequestBody ProcessoProdutivo dadosAtualizados) {
        ProcessoProdutivo processo = buscarEntidade(id);

        processo.setDescricao(dadosAtualizados.getDescricao());
        processo.setEmpresaResponsavel(dadosAtualizados.getEmpresaResponsavel());
        processo.setDataInicio(dadosAtualizados.getDataInicio());
        processo.setDataFim(dadosAtualizados.getDataFim());
        processo.setStatus(dadosAtualizados.getStatus());
        processo.setAlteradoPor(dadosAtualizados.getAlteradoPor());

        return processoProdutivoRepository.save(processo);
    }

    @PutMapping("/{id}/status")
    @Transactional
    public Operacao atualizarStatus(@PathVariable Long id, @Valid @RequestBody StatusProcessoRequest dados) {
        ProcessoProdutivo processo = buscarEntidade(id);

        processo.setStatus(dados.getStatus());
        if (dados.getStatus() != Status.NAO_INICIADO && processo.getDataInicio() == null) {
            processo.setDataInicio(LocalDate.now());
        }
        processo.setDataFim(dados.getStatus() == Status.CONCLUIDO ? LocalDate.now() : null);

        if (dados.getUsuarioId() != null) {
            usuarioRepository.findById(dados.getUsuarioId()).ifPresent(processo::setAlteradoPor);
        }
        processoProdutivoRepository.save(processo);

        Operacao operacao = operacaoRepository.findByProcessosId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Esse processo nao pertence a nenhuma operacao"));

        operacao.setStatus(calcularStatus(operacao));
        return operacaoRepository.save(operacao);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        if (!processoProdutivoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Processo nao encontrado");
        }
        processoProdutivoRepository.deleteById(id);
    }

    private ProcessoProdutivo buscarEntidade(Long id) {
        return processoProdutivoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Processo nao encontrado"));
    }

    private Status calcularStatus(Operacao operacao) {
        if (operacao.getStatus() == Status.CANCELADO) {
            return Status.CANCELADO;
        }

        List<ProcessoProdutivo> processos = operacao.getProcessos();
        if (processos.isEmpty()) {
            return operacao.getStatus();
        }

        boolean todosConcluidos = processos.stream().allMatch(p -> p.getStatus() == Status.CONCLUIDO);
        if (todosConcluidos) {
            return Status.CONCLUIDO;
        }

        boolean algumIniciado = processos.stream()
                .anyMatch(p -> p.getStatus() != null && p.getStatus() != Status.NAO_INICIADO);
        return algumIniciado ? Status.EM_ANDAMENTO : Status.NAO_INICIADO;
    }
}
