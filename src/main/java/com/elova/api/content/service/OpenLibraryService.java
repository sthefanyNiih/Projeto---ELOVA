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
 * Serviço de integração com a Open Library API (openlibrary.org).
 * Busca livros relacionados ao tema de estudo.
 * Gratuita, sem API key necessária.
 *
 * Módulo 3 - Content Engine.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OpenLibraryService {

    @Value("${elova.api.openlibrary.url}")
    private String openLibraryUrl;

    private final WebClient.Builder webClientBuilder;

    /**
     * Busca livros na Open Library relacionados ao tema.
     *
     * @param query  Termo de busca (ex: "algebra linear")
     * @param limite Número máximo de livros
     * @return Lista de ConteudoResult com título, autores e link
     */
    public List<ConteudoResult> buscar(String query, int limite) {
        List<ConteudoResult> resultados = new ArrayList<>();

        try {
            String url = openLibraryUrl + "/search.json"
                    + "?q=" + encode(query)
                    + "&limit=" + Math.min(limite, 10)
                    + "&fields=title,author_name,first_publish_year,key,cover_i,subject";

            Map<?, ?> response = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null) return resultados;

            List<?> docs = (List<?>) response.get("docs");
            if (docs == null) return resultados;

            for (Object doc : docs) {
                Map<String, Object> livro = (Map<String, Object>) doc;

                String titulo = String.valueOf(livro.getOrDefault("title", "Sem título"));

                // Autores
                List<?> autoresLista = (List<?>) livro.get("author_name");
                String autores = autoresLista != null
                        ? String.join(", ", autoresLista.stream()
                            .map(Object::toString).limit(3).toList())
                        : "Autor desconhecido";

                // Ano de publicação
                Object ano = livro.get("first_publish_year");

                // Link da obra
                String chave = String.valueOf(livro.getOrDefault("key", ""));
                String link  = "https://openlibrary.org" + chave;

                // Capa (se disponível)
                Object coverId = livro.get("cover_i");
                String thumbnail = coverId != null
                        ? "https://covers.openlibrary.org/b/id/" + coverId + "-M.jpg"
                        : null;

                String descricao = "Autor(es): " + autores
                        + (ano != null ? " · Publicado em " + ano : "");

                resultados.add(ConteudoResult.builder()
                        .titulo(titulo)
                        .descricao(descricao)
                        .url(link)
                        .tipo("LIVRO")
                        .fonte("Open Library")
                        .thumbnail(thumbnail)
                        .autor(autores)
                        .build());
            }

        } catch (Exception e) {
            log.error("[OpenLibraryService] Erro ao buscar '{}': {}", query, e.getMessage());
        }

        return resultados;
    }

    private String encode(String texto) {
        return texto.replace(" ", "+");
    }
}
