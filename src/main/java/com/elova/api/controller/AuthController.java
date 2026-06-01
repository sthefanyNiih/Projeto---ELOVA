package com.elova.api.controller;

import com.elova.api.dto.AuthDTO.*;
import com.elova.api.dto.CadastroDTO;
import com.elova.api.dto.LoginDTO;
import com.elova.api.model.usuario;
import com.elova.api.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Controller do Módulo 1 - Auth.
 *
 * Endpoints:
 *   POST /auth/cadastro          → cadastrar novo usuário
 *   POST /auth/login             → autenticar e receber JWT
 *   POST /auth/recuperar-senha   → solicitar reset de senha por e-mail
 *   POST /auth/redefinir-senha   → redefinir senha com o token recebido
 *   GET  /auth/perfil            → retorna dados do usuário autenticado (requer JWT)
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;

    // ==========================================
    // POST /auth/cadastro
    // ==========================================

    /**
     * Cadastra um novo usuário.
     *
     * Body (JSON):
     * {
     *   "nome": "Maria Silva",
     *   "email": "maria@email.com",
     *   "senha": "Senha@123",
     *   "confirmarSenha": "Senha@123"
     * }
     */
    @PostMapping("/cadastro")
    public ResponseEntity<MensagemResponse> cadastrar(@RequestBody @Valid CadastroDTO dto) {
        MensagemResponse resposta = usuarioService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    // ==========================================
    // POST /auth/login
    // ==========================================

    /**
     * Autentica o usuário e retorna o token JWT.
     *
     * Body (JSON):
     * {
     *   "email": "maria@email.com",
     *   "senha": "Senha@123"
     * }
     *
     * Resposta:
     * {
     *   "token": "eyJ...",
     *   "tipo": "Bearer",
     *   "nome": "Maria Silva",
     *   "email": "maria@email.com"
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginDTO dto) {
        return ResponseEntity.ok(usuarioService.login(dto));
    }

    // ==========================================
    // POST /auth/recuperar-senha
    // ==========================================

    /**
     * Envia e-mail com link de recuperação de senha.
     * Não revela se o e-mail existe ou não (segurança).
     *
     * Body (JSON):
     * { "email": "maria@email.com" }
     */
    @PostMapping("/recuperar-senha")
    public ResponseEntity<MensagemResponse> recuperarSenha(
            @RequestBody @Valid SolicitarResetDTO dto) {
        return ResponseEntity.ok(usuarioService.solicitarRecuperacao(dto));
    }

    // ==========================================
    // POST /auth/redefinir-senha
    // ==========================================

    /**
     * Redefine a senha usando o token do e-mail.
     *
     * Body (JSON):
     * {
     *   "token": "eyJ...",
     *   "novaSenha": "NovaSenha@456",
     *   "confirmarNovaSenha": "NovaSenha@456"
     * }
     */
    @PostMapping("/redefinir-senha")
    public ResponseEntity<MensagemResponse> redefinirSenha(
            @RequestBody @Valid ResetSenhaDTO dto) {
        return ResponseEntity.ok(usuarioService.redefinirSenha(dto));
    }

    // ==========================================
    // GET /auth/perfil  [AUTENTICADO]
    // ==========================================

    /**
     * Retorna os dados do usuário autenticado.
     * Requer header: Authorization: Bearer <token>
     */
    @GetMapping("/perfil")
    public ResponseEntity<PerfilResponse> perfil(
            @AuthenticationPrincipal UserDetails userDetails) {
        usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername());
        return ResponseEntity.ok(new PerfilResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCriadoEm().toString()
        ));
    }

    /** DTO interno de resposta do perfil. */
    record PerfilResponse(Long id, String nome, String email, String membro_desde) {}
}
