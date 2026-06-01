package com.elova.api.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção personalizada do ELOVA.
 * Carrega uma mensagem amigável e o status HTTP a ser retornado.
 */
public class ElovaException extends RuntimeException {

    private final HttpStatus status;

    public ElovaException(String mensagem, HttpStatus status) {
        super(mensagem);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
