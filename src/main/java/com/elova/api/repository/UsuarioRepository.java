package com.elova.api.repository;

import com.elova.api.model.usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository responsável pelo acesso aos dados da entidade Usuario.
 * O Spring Data JPA cria automaticamente a implementação dessa interface.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<usuario, Long> {

   /** busca usuario pelo email*/
   Optional<usuario> findByEmail(String email);

   /** verifica se ja tem um usuario com esse email*/
    boolean existsByEmail(String email);

    /** busca usuario pelo token de verificacao*/
    Optional<usuario> findByRecSenha(String recSenha);


}