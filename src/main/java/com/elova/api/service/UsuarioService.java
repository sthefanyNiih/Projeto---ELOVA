package com.elova.api.service;

import com.elova.api.dto.AuthDTO.*;
import com.elova.api.dto.CadastroDTO;
import com.elova.api.dto.LoginDTO;
import com.elova.api.exception.ElovaException;
import com.elova.api.model.usuario;
import com.elova.api.repository.UsuarioRepository;
import com.elova.api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service com toda a lógica de negócio do Módulo 1 - Auth:
 *  - Cadastro de usuário
 *  - Login (retorna JWT)
 *  - Solicitar recuperação de senha (envia e-mail)
 *  - Redefinir senha com token
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    // ==========================================
    // CADASTRO
    // ==========================================

    /**
     * Cadastra um novo usuário.
     * Valida: e-mail único, senhas conferem.
     * Salva a senha como hash BCrypt.
     */
    @Transactional
    public MensagemResponse cadastrar(CadastroDTO dto) {
        // E-mail já cadastrado?
        if (usuarioRepository.existsByEmail(dto.getEmail().toLowerCase())) {
            throw new ElovaException("Este e-mail já está cadastrado.", HttpStatus.CONFLICT);
        }

        // Senhas conferem?
        if (!dto.getSenha().equals(dto.getConfirmarSenha())) {
            throw new ElovaException("As senhas não conferem.", HttpStatus.BAD_REQUEST);
        }

        usuario usuario = com.elova.api.model.usuario
                .builder()
                .nome(dto.getNome().trim())
                .email(dto.getEmail().toLowerCase().trim())
                .senha(passwordEncoder.encode(dto.getSenha()))
                .build();

        usuarioRepository.save(usuario);

        // Envia e-mail de boas-vindas (não bloqueia se falhar)
        try {
            emailService.enviarEmailBoasVindas(usuario.getEmail(), usuario.getNome());
        } catch (Exception ignored) {
            // E-mail é opcional no fluxo de cadastro
        }

        return new MensagemResponse("Cadastro realizado com sucesso!");
    }

    // ==========================================
    // LOGIN
    // ==========================================

    /**
     * Autentica o usuário e retorna o JWT.
     * Usa o AuthenticationManager do Spring Security para
     * validar e-mail + senha (verifica BCrypt automaticamente).
     */
    public TokenResponse login(LoginDTO dto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getEmail().toLowerCase(),
                            dto.getSenha()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new ElovaException("E-mail ou senha inválidos.", HttpStatus.UNAUTHORIZED);
        }

        usuario usuario = usuarioRepository.findByEmail(dto.getEmail().toLowerCase())
                .orElseThrow(() -> new ElovaException("Usuário não encontrado.", HttpStatus.NOT_FOUND));

        String token = jwtService.gerarToken(usuario.getEmail());

        return new TokenResponse(token, "Bearer", usuario.getNome(), usuario.getEmail());
    }

    // ==========================================
    // RECUPERAÇÃO DE SENHA
    // ==========================================

    /**
     * Solicita recuperação de senha.
     * Gera um token único, salva no usuário e envia por e-mail.
     * Retorna sempre a mesma mensagem (por segurança, não revela
     * se o e-mail existe ou não).
     */
    @Transactional
    public MensagemResponse solicitarRecuperacao(SolicitarResetDTO dto) {
        usuarioRepository.findByEmail(dto.getEmail().toLowerCase()).ifPresent(usuario -> {
            String token = jwtService.gerarTokenReset(usuario.getEmail());

            usuario.setRecSenha(token);
            usuario.setTokenExpir(LocalDateTime.now().plusMinutes(30));
            usuarioRepository.save(usuario);

            try {
                emailService.enviarEmailRecuperacao(usuario.getEmail(), token);
            } catch (Exception e) {
                throw new ElovaException(
                        "Não foi possível enviar o e-mail. Tente novamente mais tarde.",
                        HttpStatus.SERVICE_UNAVAILABLE
                );
            }
        });

        // Mesma mensagem independente de o e-mail existir ou não (segurança)
        return new MensagemResponse(
                "Se este e-mail estiver cadastrado, você receberá as instruções em breve."
        );
    }

    /**
     * Redefine a senha usando o token recebido por e-mail.
     * Valida: token existente, não expirado, senhas conferem.
     */
    @Transactional
    public MensagemResponse redefinirSenha(ResetSenhaDTO dto) {
        // Senhas conferem?
        if (!dto.getNovaSenha().equals(dto.getConfirmarNovaSenha())) {
            throw new ElovaException("As senhas não conferem.", HttpStatus.BAD_REQUEST);
        }

        // Token existe no banco?
        usuario usuario = usuarioRepository.findByRecSenha(dto.getToken())
                .orElseThrow(() -> new ElovaException(
                        "Token inválido ou já utilizado.", HttpStatus.BAD_REQUEST));

        // Token expirou?
        if (usuario.getTokenExpir().isBefore(LocalDateTime.now())) {
            throw new ElovaException(
                    "Token expirado. Solicite uma nova recuperação de senha.", HttpStatus.BAD_REQUEST);
        }

        // Valida o JWT do token também
        try {
            if (!jwtService.isTokenReset(dto.getToken())) {
                throw new ElovaException("Token inválido.", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            throw new ElovaException("Token inválido ou expirado.", HttpStatus.BAD_REQUEST);
        }

        // Atualiza a senha e limpa o token
        usuario.setSenha(passwordEncoder.encode(dto.getNovaSenha()));
        usuario.setRecSenha(null);
        usuario.setTokenExpir(null);
        usuarioRepository.save(usuario);

        return new MensagemResponse("Senha redefinida com sucesso!");
    }

    // ==========================================
    // PERFIL
    // ==========================================

    /** Retorna os dados do usuário autenticado pelo e-mail. */
    public usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ElovaException("Usuário não encontrado.", HttpStatus.NOT_FOUND));
    }
}
