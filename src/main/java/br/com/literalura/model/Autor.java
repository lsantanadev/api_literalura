package br.com.literalura.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "autores")
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String nome;

    private Integer anoNascimento;

    private Integer anoFalecimento;

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<Livro> livros = new ArrayList<>();


    public Autor() {
    }

    public Autor(DadosAutor dadosAutor) {
        this.nome = dadosAutor.nomeAutor();

        try {
            this.anoNascimento = Integer.valueOf(dadosAutor.anoNascimento());
        } catch (NumberFormatException e) {
            this.anoNascimento = 0;
        }

        try {
            this.anoFalecimento = Integer.valueOf(dadosAutor.anoFalecimento());
        } catch (NumberFormatException e) {
            this.anoFalecimento = 0;
        }

    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getAnoNascimento() {
        return anoNascimento;
    }

    public void setAnoNascimento(Integer anoNascimento) {
        this.anoNascimento = anoNascimento;
    }

    public Integer getAnoFalecimento() {
        return anoFalecimento;
    }

    public void setAnoFalecimento(Integer anoFalecimento) {
        this.anoFalecimento = anoFalecimento;
    }

    public List<Livro> getLivros() {
        return livros;
    }

    public void setLivros(List<Livro> livros) {
        this.livros = livros;
    }

    public String getAnoFalecimentoFormatado() {
        if (this.anoFalecimento == null) {
            return "Desconhecido/Vivo";
        }
        return String.valueOf(this.anoFalecimento);
    }

    @Override
    public String toString() {
        List<String> titulosLivros = livros.stream()
                .map(br.com.literalura.model.Livro::getTitulo)
                .toList();
        return "Nome='" + nome + '\'' +
                ", anoNascimento=" + anoNascimento +
                ", anoFalecimento=" + anoFalecimento +
                ", livros=" + titulosLivros;
    }
}
