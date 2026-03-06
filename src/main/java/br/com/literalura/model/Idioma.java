package br.com.literalura.model;

public enum Idioma {
    EN("en"),
    PT("pt"),
    ES("es"),
    FR("fr");

    private String sigla;

    Idioma(String sigla) {
        this.sigla = sigla;
    }

    public static Idioma fromString(String text) {
        for (Idioma idioma : Idioma.values()) {
            if (idioma.sigla.equalsIgnoreCase(text)) {
                return idioma;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria de idioma encontrada para a string: " + text);
    }
}
