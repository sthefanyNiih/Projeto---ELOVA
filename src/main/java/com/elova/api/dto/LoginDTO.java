package com.elova.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** DTO pra login de usuario*/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginDTO {

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Formato de e-mail inválido.")
    @Size(max = 150, message = "E-mail deve ter no máximo 150 caracteres.")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres.")
    private String senha;

}