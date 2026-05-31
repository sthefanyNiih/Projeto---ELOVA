package com.elova.api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "materias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class materia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String assunto;

    @Column(nullable = false)
    private String horas;

    @Enumerated(EnumType.STRING)
    private DificuldadeMateria dificuldade;
}
