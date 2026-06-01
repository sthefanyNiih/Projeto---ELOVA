package com.elova.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * entidade que representa um usuario cadastrado
 */
@Entity
@Table(name = "usuarios")
@Data // Getters e setters
@NoArgsConstructor // Construtor vazio
@AllArgsConstructor // Construtor completo
@Builder // Builder do Lombok
public class usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;


    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 100)
    private String senha;

    /** token de recuperação de senha (temporario) */
    @Column(name = "rec_Senha", length = 300)
    private String recSenha;

    /**quando o token expira */
    @Column(name = "token_expir")
    private LocalDateTime tokenExpir;

    /**data de criaçao da conta */
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    protected void onCreate() {this.criadoEm = LocalDateTime.now(); }


}