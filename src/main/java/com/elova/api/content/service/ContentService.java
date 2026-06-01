package com.elova.api.content.service;

import com.elova.api.content.dto.ContentDTO.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Serviço principal do Módulo 3 - Content Engine.
 * Orquestra as chamadas às APIs externas (YouTube, Wikipedia, Open Library)
 * e filtra/retorna os conteúdos relevantes para o usuário.
 *
 * Funções do módulo:
 *  - Buscar conteúdos externos por query
 *  - Filtrar por dificuldade
 *  - Retornar materiais (vídeos, artigos, livros)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContentService {

    private final WikipediaService wikipediaService;
    private final YouTubeService youTubeService;
    private final OpenLibraryService openLibraryService;

    // ==========================================
    // BUSCA GERAL
    // ==========================================

    /**
     * Busca conteúdos em todas as fontes disponíveis.
     * O tipo filtra qual(is) APIs serão consultadas.
     *
     * Tipos aceitos: VIDEO, ARTIGO, LIVRO, WIKIPEDIA, TODOS
     */
    public BuscaResponse buscar(BuscaRequest request) {
        String query      = request.getQuery() != null ? request.getQuery().trim() : "";
        String tipo       = request.getTipo() != null ? request.getTipo().toUpperCase() : "TODOS";
        int limite        = request.getLimite() != null ? Math.min(request.getLimite(), 30) : 10;

        if (query.isBlank()) {
            return new BuscaResponse(query, 0, List.of());
        }

        List<ConteudoResult> todos = new ArrayList<>();

        // Distribui o limite entre as fontes
        int limitePorFonte = Math.max(3, limite / 3);

        switch (tipo) {
            case "VIDEO" ->
                todos.addAll(youTubeService.buscar(query, limite));

            case "ARTIGO", "WIKIPEDIA" ->
                todos.addAll(wikipediaService.buscar(query, limite));

            case "LIVRO" ->
                todos.addAll(openLibraryService.buscar(query, limite));

            default -> {
                // TODOS: consulta as três fontes em paralelo (simplificado)
                todos.addAll(youTubeService.buscar(query, limitePorFonte));
                todos.addAll(wikipediaService.buscar(query, limitePorFonte));
                todos.addAll(openLibraryService.buscar(query, limitePorFonte));
            }
        }

        // Aplica filtro de dificuldade (adapta a query se informado)
        if (request.getDificuldade() != null && !request.getDificuldade().isBlank()) {
            todos = filtrarPorDificuldade(todos, request.getDificuldade());
        }

        // Limita o total
        List<ConteudoResult> resultado = todos.stream().limit(limite).toList();

        log.info("[ContentService] Busca '{}' → {} resultados", query, resultado.size());

        return new BuscaResponse(query, resultado.size(), resultado);
    }

    // ==========================================
    // BUSCA POR MATÉRIA + TÓPICO
    // ==========================================

    /**
     * Busca conteúdos específicos para uma matéria e tópico.
     * Monta a query combinando matéria + tópico + dificuldade.
     */
    public BuscaResponse buscarPorMateria(MateriaContentRequest request) {
        StringBuilder query = new StringBuilder();
        if (request.getMateria() != null) query.append(request.getMateria());
        if (request.getTopico()  != null) query.append(" ").append(request.getTopico());

        // Adapta a query pela dificuldade
        if (request.getDificuldade() != null) {
            switch (request.getDificuldade().toUpperCase()) {
                case "FACIL"  -> query.append(" introdução básico iniciante");
                case "DIFICIL"-> query.append(" avançado aprofundado");
                default        -> query.append(" explicação");
            }
        }

        BuscaRequest buscaRequest = new BuscaRequest(
                query.toString().trim(),
                "TODOS",
                request.getLimite() != null ? request.getLimite() : 12,
                request.getDificuldade()
        );

        return buscar(buscaRequest);
    }

    // ==========================================
    // FILTRO DE DIFICULDADE
    // ==========================================

    /**
     * Filtra resultados por dificuldade com base em palavras-chave
     * presentes no título ou descrição.
     */
    private List<ConteudoResult> filtrarPorDificuldade(
            List<ConteudoResult> resultados, String dificuldade) {

        return switch (dificuldade.toUpperCase()) {
            case "FACIL" -> resultados.stream()
                    .filter(r -> contemPalavras(r, "básico", "introdução", "iniciante",
                                               "básica", "simples", "fácil"))
                    .toList();
            case "DIFICIL" -> resultados.stream()
                    .filter(r -> contemPalavras(r, "avançado", "aprofundado",
                                               "complexo", "expert", "difícil"))
                    .toList();
            default -> resultados; // MEDIO ou sem filtro específico: retorna tudo
        };
    }

    private boolean contemPalavras(ConteudoResult resultado, String... palavras) {
        String textoCompleto = (
                (resultado.getTitulo()    != null ? resultado.getTitulo()    : "") + " " +
                (resultado.getDescricao() != null ? resultado.getDescricao() : "")
        ).toLowerCase();

        for (String palavra : palavras) {
            if (textoCompleto.contains(palavra.toLowerCase())) return true;
        }
        return false;
    }
}
