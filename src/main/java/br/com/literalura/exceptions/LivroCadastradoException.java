package br.com.literalura.exceptions;

public class LivroCadastradoException extends RuntimeException {
    public LivroCadastradoException(String titulo) {
        super("O livro \"" + titulo + "\" já está cadastrado no banco de dados.");
    }
}
