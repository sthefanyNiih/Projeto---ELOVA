package com.elova.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Tratamento global de exceções.
 * Converte erros em respostas JSON padronizadas.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Erros de validação do Bean Validation (@NotBlank, @Email, @Size...).
     * Retorna 400 com mapa campo → mensagem.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> erros = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String campo = ((FieldError) error).getField();
            String mensagem = error.getDefaultMessage();
            erros.put(campo, mensagem);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("status", 400);
        response.put("erro", "Dados inválidos");
        response.put("campos", erros);
        response.put("momento", LocalDateTime.now().toString());

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Exceção personalizada do ELOVA.
     * Retorna o status e mensagem definidos na exceção.
     */
    @ExceptionHandler(ElovaException.class)
    public ResponseEntity<Map<String, Object>> handleElovaException(ElovaException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", ex.getStatus().value());
        response.put("erro", ex.getMessage());
        response.put("momento", LocalDateTime.now().toString());

        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    /**
     * Qualquer outra exceção não mapeada.
     * Retorna 500 sem expor detalhes internos.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 500);
        response.put("erro", "Ocorreu um erro interno. Tente novamente mais tarde.");
        response.put("momento", LocalDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
