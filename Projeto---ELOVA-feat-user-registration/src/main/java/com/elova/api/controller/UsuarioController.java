package com.elova.api.controller;

import jakarta.validation.Valid;
import com.elova.api.dto.UsuarioDTO;
import com.elova.api.model.usuario;
import com.elova.api.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController //classe recebe requisições e retorja diretamente em json
@RequestMapping("/usuarios") //define o caminho base da api
@RequiredArgsConstructor //lombok cria o construtor com os atributos final
public class UsuarioController {

    private final UsuarioService service;

    @PostMapping
    public ResponseEntity<usuario> criar(@RequestBody @Valid UsuarioDTO dto) {
        return ResponseEntity.ok(service.criarUsuario(dto));
    }
}
