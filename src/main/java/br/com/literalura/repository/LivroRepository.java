package br.com.literalura.repository;

import br.com.literalura.model.Idioma;
import br.com.literalura.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    Optional<Livro> findByTituloIgnoreCase(String nomeLivro);

    List<Livro> findByIdioma(Idioma idioma);
}
