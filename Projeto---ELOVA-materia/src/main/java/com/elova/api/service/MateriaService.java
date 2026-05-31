package com.elova.api.service;

import com.elova.api.dto.MateriaDTO;
import com.elova.api.model.materia;
import com.elova.api.repository.MateriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MateriaService {

    private final MateriaRepository repository;

    public materia criarMateria(MateriaDTO dto) {

        repository.findByNome(dto.getNome())
                .ifPresent(u -> {
                    throw new RuntimeException("Matéria já cadastrada");
                });

        materia materia = com.elova.api.model.materia.builder()
                .nome(dto.getNome())
                .assunto(dto.getAssunto())
                .horas(String.valueOf(dto.getHoras()))
                .dificuldade(dto.getDificuldade())
                .build();

        return repository.save(materia);
    }

}
