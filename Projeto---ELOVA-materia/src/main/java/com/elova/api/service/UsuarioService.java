
package com.elova.api.service;

import com.elova.api.dto.UsuarioDTO;
import com.elova.api.model.usuario;
import com.elova.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;

    public usuario criarUsuario(UsuarioDTO dto) {

        // validação: senha e confirmação iguais
        if (!dto.getSenha().equals(dto.getConfirmarSenha())) {
            throw new RuntimeException("As senhas não coincidem");
        }

        // validação: email já existe
        repository.findByEmail(dto.getEmail())
                .ifPresent(u -> {
                    throw new RuntimeException("Email já cadastrado");
                });

        // criação do usuário (SEM erro de inicialização)
        usuario usuario = com.elova.api.model.usuario.builder()
                .email(dto.getEmail())
                .senha(dto.getSenha())
                .build();

        // salva no banco
        return repository.save(usuario);
    }
}