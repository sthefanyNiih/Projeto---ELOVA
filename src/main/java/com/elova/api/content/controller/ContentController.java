package com.elova.api.content.controller;

import com.elova.api.content.dto.ContentDTO.*;
import com.elova.api.content.service.ContentService;
import com.elova.api.content.service.WikipediaService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller do Módulo 3 - Content Engine.
 * Todos os endpoints requerem autenticação (JWT).
 *
 * Endpoints:
 *   GET  /content/buscar              → busca conteúdos por query e tipo
 *   GET  /content/materia             → busca conteúdos para uma matéria/tópico
 *   GET  /content/wikipedia/{titulo}  → resumo de artigo específico da Wikipedia
 */
@RestController
@RequestMapping("/content")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;
    private final WikipediaService wikipediaService;

    // ==========================================
    // GET /content/buscar
    // ==========================================

    /**
     * Busca conteúdos educacionais em todas as fontes externas.
     *
     * Parâmetros:
     *   q          (obrigatório) → termo de busca, ex: "equações do 2º grau"
     *   tipo       (opcional)   → VIDEO | ARTIGO | LIVRO | WIKIPEDIA | TODOS (default: TODOS)
     *   limite     (opcional)   → quantidade de resultados (default: 10, max: 30)
     *   dificuldade(opcional)   → FACIL | MEDIO | DIFICIL
     *
     * Exemplos:
     *   GET /content/buscar?q=algebra+linear
     *   GET /content/buscar?q=fotossíntese&tipo=VIDEO&limite=5
     *   GET /content/buscar?q=história+do+brasil&tipo=TODOS&dificuldade=FACIL
     */
    @GetMapping("/buscar")
    public ResponseEntity<BuscaResponse> buscar(
            @RequestParam @NotBlank @Size(max = 200) String q,
            @RequestParam(required = false, defaultValue = "TODOS") String tipo,
            @RequestParam(required = false, defaultValue = "10")    Integer limite,
            @RequestParam(required = false)                          String dificuldade
    ) {
        BuscaRequest request = new BuscaRequest(q, tipo, limite, dificuldade);
        return ResponseEntity.ok(contentService.buscar(request));
    }

    // ==========================================
    // GET /content/materia
    // ==========================================

    /**
     * Busca conteúdos específicos para uma matéria e tópico do plano de estudos.
     *
     * Parâmetros:
     *   materia     (obrigatório) → nome da matéria, ex: "Matemática"
     *   topico      (opcional)   → tópico específico, ex: "Funções quadráticas"
     *   dificuldade (opcional)   → FACIL | MEDIO | DIFICIL
     *   limite      (opcional)   → quantidade de resultados (default: 12)
     *
     * Exemplos:
     *   GET /content/materia?materia=Física&topico=Cinemática
     *   GET /content/materia?materia=Biologia&topico=Células&dificuldade=FACIL
     */
    @GetMapping("/materia")
    public ResponseEntity<BuscaResponse> buscarPorMateria(
            @RequestParam @NotBlank @Size(max = 100) String materia,
            @RequestParam(required = false)           String topico,
            @RequestParam(required = false)           String dificuldade,
            @RequestParam(required = false, defaultValue = "12") Integer limite
    ) {
        MateriaContentRequest request = new MateriaContentRequest(
                materia, topico, dificuldade, limite
        );
        return ResponseEntity.ok(contentService.buscarPorMateria(request));
    }

    // ==========================================
    // GET /content/wikipedia/{titulo}
    // ==========================================

    /**
     * Retorna o resumo de um artigo específico da Wikipedia em português.
     *
     * Exemplo:
     *   GET /content/wikipedia/Álgebra linear
     *   GET /content/wikipedia/Segunda Guerra Mundial
     */
    @GetMapping("/wikipedia/{titulo}")
    public ResponseEntity<?> resumoWikipedia(
            @PathVariable @NotBlank @Size(max = 200) String titulo
    ) {
        var resultado = wikipediaService.buscarResumo(titulo);

        if (resultado == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(resultado);
    }
}
