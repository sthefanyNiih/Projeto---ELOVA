package com.elova.api.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO para cadastro de novo usuário.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CadastroDTO {

    /** validacao do nome*/
    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 4, max = 100, message = "O nome deve ter entre 4 e 100 caracteres.")

    /** validacao pra acietar apenas letras*/
    @Pattern(regexp = "^[\\p{L} ]+$", message = "O nome deve conter apenas letras."
    )
    private String nome;
    /** validacao para o email*/
    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Formato de e-mail inválido.")
    @Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres.")
    private String email;

    /** validacao para a senha*/
    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$", message = "A senha deve conter pelo menos 1 letra maiúscula e 1 caractere especial."
    )
    private String senha;

    /** validacao para confirmar a senha*/
    @NotBlank(message = "A confirmação de senha é obrigatória.")
    @Size(max = 72, message = "A confirmação de senha deve ter no máximo 72 caracteres.")
    private String confirmarSenha;
}
