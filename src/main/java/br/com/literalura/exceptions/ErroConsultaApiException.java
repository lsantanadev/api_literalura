package br.com.literalura.exceptions;

public class ErroConsultaApiException extends RuntimeException {
    public ErroConsultaApiException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }

    public ErroConsultaApiException(String mensagem) {
        super(mensagem);
    }
}