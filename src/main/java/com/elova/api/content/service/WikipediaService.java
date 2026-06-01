package com.elova.api.content.service;

import com.elova.api.content.dto.ContentDTO.ConteudoResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Serviço de integração com a API REST da Wikipedia em português.
 * Gratuita, sem necessidade de API key.
 *
 * Módulo 3 - Content Engine.
 * Endpoint usado: https://pt.wikipedia.org/api/rest_v1/page/summary/{titulo}
 *                 https://pt.wikipedia.org/w/api.php (busca)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WikipediaService {

    @Value("${elova.api.wikipedia.url}")
    private String wikipediaUrl;

    private final WebClient.Builder webClientBuilder;

    /**
     * Busca artigos na Wikipedia em português pelo termo fornecido.
     *
     * @param query  Termo de busca (ex: "Álgebra linear")
     * @param limite Número máximo de resultados
     * @return Lista de ConteudoResult com título, resumo e link
     */
    public List<ConteudoResult> buscar(String query, int limite) {
        List<ConteudoResult> resultados = new ArrayList<>();

        try {
            // Usa a API de busca da Wikipedia
            String url = "https://pt.wikipedia.org/w/api.php" +
                    "?action=query" +
                    "&list=search" +
                    "&srsearch=" + encode(query) +
                    "&srlimit=" + Math.min(limite, 10) +
                    "&format=json" +
                    "&utf8=1";

            Map<?, ?> response = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null) return resultados;

            Map<?, ?> queryMap = (Map<?, ?>) response.get("query");
            if (queryMap == null) return resultados;

            List<?> searchResults = (List<?>) queryMap.get("search");
            if (searchResults == null) return resultados;

            for (Object item : searchResults) {
                Map<?, ?> artigo = (Map<?, ?>) item;
                String titulo = String.valueOf(artigo.get("title"));
                String snippet = String.valueOf(artigo.get("snippet"))
                        .replaceAll("<[^>]*>", ""); // Remove tags HTML

                String linkArtigo = "https://pt.wikipedia.org/wiki/"
                        + titulo.replace(" ", "_");

                resultados.add(ConteudoResult.builder()
                        .titulo(titulo)
                        .descricao(snippet)
                        .url(linkArtigo)
                        .tipo("ARTIGO")
                        .fonte("Wikipedia")
                        .thumbnail(null)
                        .autor(null)
                        .build());
            }

        } catch (Exception e) {
            log.error("[WikipediaService] Erro ao buscar '{}': {}", query, e.getMessage());
        }

        return resultados;
    }

    /**
     * Busca o resumo completo de um artigo específico da Wikipedia.
     *
     * @param tituloArtigo Título exato do artigo (ex: "Álgebra linear")
     */
    public ConteudoResult buscarResumo(String tituloArtigo) {
        try {
            String url = wikipediaUrl + "/page/summary/" + encode(tituloArtigo);

            Map<?, ?> response = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null) return null;

            String titulo    = String.valueOf(response.get("title"));
            String resumo    = String.valueOf(response.get("extract"));
            String link      = String.valueOf(((Map<?, ?>) response.get("content_urls"))
                                 != null ? ((Map<?, ?>) ((Map<?, ?>) response.get("content_urls"))
                                 .get("desktop")).get("page") : "");
            Map<?, ?> thumb  = (Map<?, ?>) response.get("thumbnail");
            String thumbnail = thumb != null ? String.valueOf(thumb.get("source")) : null;

            return ConteudoResult.builder()
                    .titulo(titulo)
                    .descricao(resumo)
                    .url(link)
                    .tipo("ARTIGO")
                    .fonte("Wikipedia")
                    .thumbnail(thumbnail)
                    .build();

        } catch (Exception e) {
            log.error("[WikipediaService] Erro ao buscar resumo '{}': {}", tituloArtigo, e.getMessage());
            return null;
        }
    }

    private String encode(String texto) {
        return texto.replace(" ", "%20");
    }
}
