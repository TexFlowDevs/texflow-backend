package com.texflow.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.texflow.backend.dto.AlterarSenhaRequest;
import com.texflow.backend.dto.AtualizarUsuarioRequest;
import com.texflow.backend.dto.NovoUsuarioRequest;
import com.texflow.backend.dto.UsuarioResponse;
import com.texflow.backend.model.Usuario;
import com.texflow.backend.repository.UsuarioRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream().map(UsuarioResponse::new).toList();
    }

    @GetMapping("/{id}")
    public UsuarioResponse buscar(@PathVariable Long id) {
        return new UsuarioResponse(buscarEntidade(id));
    }

    @PostMapping
    public UsuarioResponse criar(@Valid @RequestBody NovoUsuarioRequest dados) {
        if (usuarioRepository.existsByEmail(dados.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ja existe um usuario com esse e-mail");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dados.getNome());
        usuario.setEmail(dados.getEmail());
        usuario.setSenha(passwordEncoder.encode(dados.getSenha()));
        usuario.setTipo(dados.getTipo());

        return new UsuarioResponse(usuarioRepository.save(usuario));
    }

    @PutMapping("/{id}")
    public UsuarioResponse atualizar(@PathVariable Long id, @Valid @RequestBody AtualizarUsuarioRequest dados) {
        Usuario usuario = buscarEntidade(id);

        usuario.setNome(dados.getNome());
        usuario.setEmail(dados.getEmail());

        return new UsuarioResponse(usuarioRepository.save(usuario));
    }

    @PutMapping("/{id}/senha")
    public Map<String, String> alterarSenha(@PathVariable Long id, @Valid @RequestBody AlterarSenhaRequest dados) {
        Usuario usuario = buscarEntidade(id);

        if (!passwordEncoder.matches(dados.getSenhaAtual(), usuario.getSenha())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A senha atual esta incorreta");
        }

        usuario.setSenha(passwordEncoder.encode(dados.getNovaSenha()));
        usuarioRepository.save(usuario);

        return Map.of("mensagem", "Senha alterada com sucesso");
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    private Usuario buscarEntidade(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado"));
    }
}
