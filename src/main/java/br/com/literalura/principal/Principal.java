package br.com.literalura.principal;

import br.com.literalura.exceptions.ErroConsultaApiException;
import br.com.literalura.exceptions.LivroCadastradoException;
import br.com.literalura.exceptions.ErroConsultaApiException;
import br.com.literalura.model.*;
import br.com.literalura.repository.AutorRepository;
import br.com.literalura.repository.LivroRepository;
import br.com.literalura.service.ConsumoAPI;
import br.com.literalura.service.ConverteDados;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {

    private final LivroRepository repositorio;

    private final AutorRepository autorRepositorio;

    private Scanner leitura = new Scanner(System.in);

    private ConsumoAPI consumo = new ConsumoAPI();

    private ConverteDados conversor = new ConverteDados();

    private static final String ENDERECO = "https://gutendex.com/books/";

    private Optional<Livro> livroBusca;

    private List<Livro> livros = new ArrayList<>();

    private List<Autor> autores = new ArrayList<>();

    public Principal(LivroRepository repositorio, AutorRepository autorRepositorio) {
        this.repositorio = repositorio;
        this.autorRepositorio = autorRepositorio;
    }

    public void exibeMenu() {
        var opcao = 0;

        while (opcao != 7) {
            String menu = """
                     Escolha o número da sua opção:\s
                    \s
                     1 - Buscar livro pelo título
                     2 - Buscar livros
                     3 - Listar livros registrados
                     4 - Listar autores registrados
                     5 - Listar autores vivos em determinado ano
                     6 - Listar livros em um determinado idioma
                     7 - Sair       \s
                    \s""";

            System.out.println(menu);

            try {
                opcao = leitura.nextInt();
                leitura.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Opção inválida. Por favor, digite um número entre 1 e 7.");
                leitura.nextLine(); // limpa o buffer
                continue;
            }

            switch (opcao) {
                case 1 -> buscarLivroPorTitulo();
                case 2 -> buscarLivroWeb();
                case 3 -> listarLivrosRegistrados();
                case 4 -> listarAutoresRegistrados();
                case 5 -> listarAutoresVivos();
                case 6 -> listarLivrosPorIdioma();
                case 7 -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida. Escolha entre 1 e 7.");
            }
        }
    }

    private void listarLivrosPorIdioma() {
        System.out.println("Digite o idioma (sigla) que deseja pesquisar: (en, pt, es, fr)");
        var sigla = leitura.nextLine();

        try {
            Idioma idioma = Idioma.fromString(sigla);
            List<Livro> livrosEncontrados = repositorio.findByIdioma(idioma);

            if (!livrosEncontrados.isEmpty()) {
                livrosEncontrados.forEach(System.out::println);
            } else {
                System.out.println("Nenhum livro encontrado no banco de dados com esse idioma.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Idioma não reconhecido: \"" + sigla + "\". Use uma das siglas: en, pt, es, fr.");
        }
    }

    private void listarAutoresVivos() {
        System.out.println("Listar autores vivos em determinado ano: ");

        int ano;
        try {
            ano = leitura.nextInt();
            leitura.nextLine(); // consumir o '\n' restante
        } catch (InputMismatchException e) {
            System.out.println("Ano inválido. Por favor, digite um número inteiro (ex: 1850).");
            leitura.nextLine(); // limpa o buffer
            return;
        }

        List<Autor> autoresEncontrados = autorRepositorio.buscarAutoresVivosNoAno(ano);

        if (!autoresEncontrados.isEmpty()) {
            autoresEncontrados.forEach(a ->
                    System.out.printf("Autor: %s | Ano Nascimento: %d | Ano Falecimento: %s%n",
                            a.getNome(), a.getAnoNascimento(), a.getAnoFalecimentoFormatado()));
        } else {
            System.out.println("Nenhum autor encontrado vivo no ano " + ano + ".");
        }
    }

    private void listarAutoresRegistrados() {
        autores = autorRepositorio.findAll();

        if (!autores.isEmpty()) {
            autores.forEach(System.out::println);
        } else {
            System.out.println("Nenhum autor registrado no banco.");
        }
    }

    private void listarLivrosRegistrados() {
        livros = repositorio.findAll();

        if (!livros.isEmpty()) {
            livros.forEach(l -> System.out.printf(
                    "Titulo: %s   |  Idioma: %s  |  Autor: %s%n",
                    l.getTitulo(), l.getIdioma(), l.getAutor() != null ? l.getAutor().getNome() : "Desconhecido"));
        } else {
            System.out.println("Nenhum livro registrado no banco.");
        }
    }

    private void buscarLivroWeb() {
        DadosLivro dados;
        try {
            dados = getDadosLivro();
        } catch (ErroConsultaApiException e) {
            System.out.println("Não foi possível conectar à API: " + e.getMessage());
            return;
        }

        if (dados == null) {
            return;
        }

        Livro livro;
        try {
            livro = new Livro(dados);
        } catch (IllegalArgumentException e) {
            System.out.println("Idioma do livro não é suportado: " + e.getMessage());
            return;
        }

        if (dados.autor() != null && !dados.autor().isEmpty()) {
            DadosAutor dadosAutor = dados.autor().get(0);
            Autor autor = new Autor();
            autor.setNome(dadosAutor.nomeAutor());

            try {
                autor.setAnoNascimento(Integer.parseInt(dadosAutor.anoNascimento()));
            } catch (NumberFormatException e) {
                autor.setAnoNascimento(0);
            }

            try {
                autor.setAnoFalecimento(Integer.parseInt(dadosAutor.anoFalecimento()));
            } catch (NumberFormatException e) {
                autor.setAnoFalecimento(0);
            }

            livro.setAutor(autor);
        }

        try {
            repositorio.save(livro);
            System.out.println("Livro salvo com sucesso: " + livro.getTitulo());
        } catch (DataIntegrityViolationException e) {
            throw new LivroCadastradoException(livro.getTitulo());
        } catch (LivroCadastradoException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro inesperado ao salvar o livro: " + e.getMessage());
        }
    }

    private DadosLivro getDadosLivro() {
        System.out.println("Digite o nome do livro a ser buscado: ");
        var nomeLivro = leitura.nextLine();

        if (nomeLivro == null || nomeLivro.isBlank()) {
            System.out.println("O nome do livro não pode ser vazio.");
            return null;
        }

        String json;
        try {
            json = consumo.obterDados(ENDERECO + "?search=" + nomeLivro.toLowerCase().replace(" ", "+"));
        } catch (RuntimeException e) {
            throw new ErroConsultaApiException("Falha ao acessar a API Gutendex. Verifique sua conexão.", e);
        }

        DadosResposta dadosResposta;
        try {
            dadosResposta = conversor.obterDados(json, DadosResposta.class);
        } catch (RuntimeException e) {
            throw new ErroConsultaApiException("Erro ao processar a resposta da API.", e);
        }

        if (dadosResposta.resultados() != null && !dadosResposta.resultados().isEmpty()) {
            return dadosResposta.resultados().get(0);
        } else {
            System.out.println("Nenhum livro encontrado para: \"" + nomeLivro + "\".");
            return null;
        }
    }

    private void buscarLivroPorTitulo() {
        System.out.println("Digite o nome do livro a ser buscado: ");
        var nomeLivro = leitura.nextLine();

        if (nomeLivro == null || nomeLivro.isBlank()) {
            System.out.println("O nome do livro não pode ser vazio.");
            return;
        }

        livroBusca = repositorio.findByTituloIgnoreCase(nomeLivro);

        if (livroBusca.isPresent()) {
            System.out.println("Livro encontrado: " + livroBusca.get());
        } else {
            System.out.println("Nenhum livro encontrado com o título: \"" + nomeLivro + "\".");
        }
    }
}