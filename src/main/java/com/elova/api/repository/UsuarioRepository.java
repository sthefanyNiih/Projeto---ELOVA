package com.elova.api.repository;

import com.elova.api.model.usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository responsável pelo acesso aos dados da entidade Usuario.
 * O Spring Data JPA cria automaticamente a implementação dessa interface.
 */
public interface UsuarioRepository extends JpaRepository<usuario, Long> {

    /**
     * Busca um usuário pelo email.
     *
     * @param email Email do usuário
     * @return Optional contendo o usuário, se encontrado
     */
    Optional<usuario> findByEmail(String email);
}