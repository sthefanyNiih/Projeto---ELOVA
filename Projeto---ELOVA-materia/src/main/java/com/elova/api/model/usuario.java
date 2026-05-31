package com.elova.api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity // Define como tabela do banco
@Table(name = "usuarios") // Nome da tabela
@Data // Getters e setters
@NoArgsConstructor // Construtor vazio
@AllArgsConstructor // Construtor completo
@Builder // Builder do Lombok
public class usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String senha;
}