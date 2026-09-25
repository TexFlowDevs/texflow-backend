package com.texflow.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.texflow.backend.model.Operacao;
import com.texflow.backend.repository.OperacaoRepository;

@RestController
@RequestMapping("/api/operacoes")
public class OperacaoController {

    private final OperacaoRepository operacaoRepository;

    public OperacaoController(OperacaoRepository operacaoRepository) {
        this.operacaoRepository = operacaoRepository;
    }

    @GetMapping
    public List<Operacao> listar() {
        return operacaoRepository.findAll();
    }

    @GetMapping("/{id}")
    public Operacao buscar(@PathVariable Long id) {
        return operacaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Operacao nao encontrada"));
    }

    @PostMapping
    public Operacao criar(@RequestBody Operacao operacao) {
        return operacaoRepository.save(operacao);
    }

    @PutMapping("/{id}")
    public Operacao atualizar(@PathVariable Long id, @RequestBody Operacao dadosAtualizados) {
        Operacao operacao = operacaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Operacao nao encontrada"));

        operacao.setReferencia(dadosAtualizados.getReferencia());
        operacao.setCliente(dadosAtualizados.getCliente());
        operacao.setDataEntrega(dadosAtualizados.getDataEntrega());
        operacao.setStatus(dadosAtualizados.getStatus());

        return operacaoRepository.save(operacao);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        if (!operacaoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Operacao nao encontrada");
        }
        operacaoRepository.deleteById(id);
    }
}
