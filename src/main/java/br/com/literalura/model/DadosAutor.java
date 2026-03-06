package br.com.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosAutor(@JsonAlias("name") String nomeAutor,
                         @JsonAlias("birth_year") String anoNascimento,
                         @JsonAlias("death_year") String anoFalecimento) {
}
