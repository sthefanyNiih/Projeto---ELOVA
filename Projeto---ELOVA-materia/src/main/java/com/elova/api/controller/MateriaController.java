package com.elova.api.controller;

import jakarta.validation.Valid;
import com.elova.api.dto.MateriaDTO;
import com.elova.api.model.materia;
import com.elova.api.service.MateriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/materias")
@RequiredArgsConstructor
public class MateriaController {

    private final MateriaService service;

    @PostMapping
    public ResponseEntity<materia> criar(@RequestBody @Valid MateriaDTO dto) {
        return ResponseEntity.ok(service.criarMateria(dto));
    }
}
