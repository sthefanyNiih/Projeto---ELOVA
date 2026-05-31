package com.elova.api.dto;

import com.elova.api.model.DificuldadeMateria;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class MateriaDTO {

    @NotBlank(message = "O nome da matéria é obrigatório.")
    private String nome;

    @NotBlank(message = "O assunto da matéria é obrigatório.")
    private String assunto;

    @NotBlank(message = "As horas que um assunto possue são obrigatórias.")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horas;

    @NotNull(message = "A dificuldade da matéria é obrigatória.")
    private DificuldadeMateria dificuldade;

}
