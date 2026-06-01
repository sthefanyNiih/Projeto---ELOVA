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
 * Serviço de integração com a YouTube Data API v3.
 * Busca vídeos educacionais relevantes para o tema de estudo.
 *
 * Módulo 3 - Content Engine.
 * Requer: elova.api.youtube.key no application.properties
 *
 * Limite gratuito: 10.000 unidades/dia.
 * Uma busca custa 100 unidades.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class YouTubeService {

    @Value("${elova.api.youtube.key}")
    private String apiKey;

    private final WebClient.Builder webClientBuilder;

    private static final String YOUTUBE_SEARCH_URL =
            "https://www.googleapis.com/youtube/v3/search";

    /**
     * Busca vídeos no YouTube relacionados ao tema de estudo.
     *
     * @param query     Termo de busca (ex: "Equações do 2º grau explicação")
     * @param limite    Número máximo de vídeos (máx 50, padrão 5)
     * @return Lista de ConteudoResult com título, descrição, link e thumbnail
     */
    public List<ConteudoResult> buscar(String query, int limite) {
        List<ConteudoResult> resultados = new ArrayList<>();

        if (apiKey == null || apiKey.isBlank() || apiKey.equals("SUA_YOUTUBE_API_KEY")) {
            log.warn("[YouTubeService] API key não configurada. Pulando busca no YouTube.");
            return resultados;
        }

        try {
            String url = YOUTUBE_SEARCH_URL
                    + "?part=snippet"
                    + "&q=" + encode(query + " educação")
                    + "&type=video"
                    + "&relevanceLanguage=pt"
                    + "&maxResults=" + Math.min(limite, 10)
                    + "&key=" + apiKey;

            Map<?, ?> response = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null) return resultados;

            List<?> items = (List<?>) response.get("items");
            if (items == null) return resultados;

            for (Object item : items) {
                Map<?, ?> video    = (Map<?, ?>) item;
                Map<?, ?> idMap    = (Map<?, ?>) video.get("id");
                Map<?, ?> snippet  = (Map<?, ?>) video.get("snippet");

                if (idMap == null || snippet == null) continue;

                String videoId    = String.valueOf(idMap.get("videoId"));
                String titulo     = String.valueOf(snippet.get("title"));
                String descricao  = String.valueOf(snippet.get("description"));
                String canal      = String.valueOf(snippet.get("channelTitle"));
                Map<?, ?> thumbs  = (Map<?, ?>) snippet.get("thumbnails");
                String thumbnail  = thumbs != null
                        ? String.valueOf(((Map<?, ?>) thumbs.get("medium")).get("url"))
                        : null;

                resultados.add(ConteudoResult.builder()
                        .titulo(titulo)
                        .descricao(descricao)
                        .url("https://www.youtube.com/watch?v=" + videoId)
                        .tipo("VIDEO")
                        .fonte("YouTube")
                        .thumbnail(thumbnail)
                        .autor(canal)
                        .build());
            }

        } catch (Exception e) {
            log.error("[YouTubeService] Erro ao buscar '{}': {}", query, e.getMessage());
        }

        return resultados;
    }

    private String encode(String texto) {
        return texto.replace(" ", "%20").replace("º", "%BA");
    }
}
