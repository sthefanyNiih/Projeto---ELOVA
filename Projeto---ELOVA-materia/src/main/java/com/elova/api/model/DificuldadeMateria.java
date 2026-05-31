package com.elova.api.model;

public enum DificuldadeMateria {

    FACIL("Fácil"),
    MEDIA("Média"),
    DIFICIL("Difícil");

    private String descricao;

    DificuldadeMateria(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
