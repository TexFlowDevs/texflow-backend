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

import com.texflow.backend.model.Referencia;
import com.texflow.backend.repository.ReferenciaRepository;

@RestController
@RequestMapping("/api/referencias")
public class ReferenciaController {

    private final ReferenciaRepository referenciaRepository;

    public ReferenciaController(ReferenciaRepository referenciaRepository) {
        this.referenciaRepository = referenciaRepository;
    }

    @GetMapping
    public List<Referencia> listar() {
        return referenciaRepository.findAll();
    }

    @GetMapping("/{id}")
    public Referencia buscar(@PathVariable Long id) {
        return referenciaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Referencia nao encontrada"));
    }

    @PostMapping
    public Referencia criar(@RequestBody Referencia referencia) {
        return referenciaRepository.save(referencia);
    }

    @PutMapping("/{id}")
    public Referencia atualizar(@PathVariable Long id, @RequestBody Referencia dadosAtualizados) {
        Referencia referencia = referenciaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Referencia nao encontrada"));

        referencia.setNome(dadosAtualizados.getNome());

        return referenciaRepository.save(referencia);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        if (!referenciaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Referencia nao encontrada");
        }
        referenciaRepository.deleteById(id);
    }
}
