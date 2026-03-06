package br.com.literalura.demo.service;

public interface IconverteDados {
    <T> T obterDados(String json, Class<T> classe);
}
