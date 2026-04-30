package com.elova.api.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data// Gera getters e setters automaticamente
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UsuarioDTO {


    @NotBlank(message = "O Email é obrigatório.") //campo não pode ser vazio
    @Email(message = "Formato de email inválido.")//exige frormato de email
    private String email; // Email vindo do app (Flutter, Postman, etc)

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$", //precisa conter carac. esp. e 1 maius.
            message = "A senha deve conter pelo menos 1 letra maiúscula e 1 caractere especial")
    private String senha; // Senha digitada pelo usuário

    @NotBlank(message = "A Confirmação da senha é obrigatória.")
    private String confirmarSenha;  // Campo usado apenas para validação
    // NÃO vai para o banco
}
