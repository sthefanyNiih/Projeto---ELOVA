package com.elova.api.repository;

import com.elova.api.model.materia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MateriaRepository extends JpaRepository<materia, Long> {

    Optional<materia> findByNome(String nome);

}
