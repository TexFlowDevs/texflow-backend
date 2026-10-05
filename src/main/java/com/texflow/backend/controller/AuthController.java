package com.texflow.backend.controller;

import java.security.SecureRandom;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.texflow.backend.dto.EsqueciSenhaRequest;
import com.texflow.backend.dto.LoginRequest;
import com.texflow.backend.dto.UsuarioResponse;
import com.texflow.backend.model.Usuario;
import com.texflow.backend.repository.UsuarioRepository;
import com.texflow.backend.service.EmailService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String CARACTERES_SENHA = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
            EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @PostMapping("/login")
    public UsuarioResponse login(@Valid @RequestBody LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha invalidos"));

        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha invalidos");
        }

        return new UsuarioResponse(usuario);
    }

    @PostMapping("/esqueci-senha")
    public Map<String, String> esqueciSenha(@Valid @RequestBody EsqueciSenhaRequest request) {
        usuarioRepository.findByEmail(request.getEmail()).ifPresent(usuario -> {
            String senhaNova = gerarSenhaAleatoria();
            usuario.setSenha(passwordEncoder.encode(senhaNova));
            usuarioRepository.save(usuario);
            emailService.enviarSenhaNova(usuario.getEmail(), usuario.getNome(), senhaNova);
        });

        return Map.of("mensagem", "Se existir uma conta com esse e-mail, enviamos uma nova senha pra ele");
    }

    private String gerarSenhaAleatoria() {
        StringBuilder senha = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            senha.append(CARACTERES_SENHA.charAt(RANDOM.nextInt(CARACTERES_SENHA.length())));
        }
        return senha.toString();
    }
}
