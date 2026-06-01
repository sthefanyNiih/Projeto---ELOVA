package com.elova.api.content.dto;

import lombok.*;
import java.util.List;

/**
 * DTOs do Módulo 3 - Content Engine.
 * Representam os dados retornados pelas APIs externas
 * de forma padronizada para o frontend.
 */
public class ContentDTO {

    // ==========================================
    // RESULTADO GENÉRICO DE CONTEÚDO
    // ==========================================

    /**
     * Resultado unificado de qualquer fonte de conteúdo externo.
     * Facilita o consumo no frontend independente da API de origem.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ConteudoResult {
        private String titulo;
        private String descricao;
        private String url;
        private String tipo;      // VIDEO, ARTIGO, LIVRO, WIKIPEDIA
        private String fonte;     // YouTube, Wikipedia, Open Library, Google
        private String thumbnail; // URL da imagem (quando disponível)
        private String autor;     // Autor ou canal (quando disponível)
    }

    // ==========================================
    // BUSCA
    // ==========================================

    /** Request para buscar conteúdos. */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BuscaRequest {
        private String query;         // Termo de busca
        private String tipo;          // VIDEO, ARTIGO, LIVRO, WIKIPEDIA, TODOS
        private Integer limite;       // Quantos resultados (default: 10)
        private String dificuldade;   // FACIL, MEDIO, DIFICIL (opcional)
    }

    /** Resposta paginada de conteúdos. */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BuscaResponse {
        private String query;
        private Integer total;
        private List<ConteudoResult> resultados;
    }

    // ==========================================
    // MATÉRIA
    // ==========================================

    /** Request para buscar conteúdos de uma matéria. */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MateriaContentRequest {
        private String materia;       // Ex: "Matemática"
        private String topico;        // Ex: "Equações do 2º grau"
        private String dificuldade;   // FACIL, MEDIO, DIFICIL
        private Integer limite;
    }
}
