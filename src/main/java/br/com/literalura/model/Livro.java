package br.com.literalura.model;

import jakarta.persistence.*;

@Entity
@Table(name = "livros")
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String titulo;

    private Idioma idioma;

    @ManyToOne(cascade = CascadeType.ALL)
    private Autor autor;

    public Livro() {
    }

    public Livro(DadosLivro dadosLivro) {
        this.titulo = dadosLivro.titulo();

        if (dadosLivro.idiomas() != null && !dadosLivro.idiomas().isEmpty()) {
            // Lança IllegalArgumentException se o idioma não for suportado —
            // tratado no chamador (Principal.buscarLivroWeb)
            this.idioma = Idioma.fromString(dadosLivro.idiomas().get(0));
        }

        if (dadosLivro.autor() != null && !dadosLivro.autor().isEmpty()) {
            this.autor = new Autor(dadosLivro.autor().get(0));
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Idioma getIdioma() {
        return idioma;
    }

    public void setIdioma(Idioma idioma) {
        this.idioma = idioma;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    @Override
    public String toString() {
        return "titulo='" + titulo + '\'' +
                ", idioma=" + idioma +
                ", autor=" + (autor != null ? autor.getNome() : "Desconhecido");
    }
}