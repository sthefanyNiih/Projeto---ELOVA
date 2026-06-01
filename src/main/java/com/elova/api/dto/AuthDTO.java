package com.elova.api.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTOs auxiliares
 */
public class AuthDTO {

    /** Resposta retornada após login bem sucedido. */
    @Data
    @AllArgsConstructor
    public static class TokenResponse {
        private String token;
        private String tipo;   // "Bearer"
        private String nome;
        private String email;
    }

    /** Solicitar recuperação de senha (envia e-mail). */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SolicitarResetDTO {
        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "Formato de e-mail inválido.")
        @Size(max = 150)
        private String email;
    }

    /** Redefinir a senha com o token recebido por e-mail. */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResetSenhaDTO {

        @NotBlank(message = "O token é obrigatório.")
        private String token;

        @NotBlank(message = "A nova senha é obrigatória.")
        @Size(min = 8, max = 72,
              message = "A senha deve ter entre 8 e 72 caracteres.")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$", message = "A senha deve conter pelo menos 1 letra maiúscula e 1 caractere especial."
        )
        private String novaSenha;

        @NotBlank(message = "A confirmação de senha é obrigatória.")
        @Size(max = 72)
        private String confirmarNovaSenha;
    }

    /** Resposta genérica de sucesso/mensagem. */
    @Data
    @AllArgsConstructor
    public static class MensagemResponse {
        private String mensagem;
    }
}
