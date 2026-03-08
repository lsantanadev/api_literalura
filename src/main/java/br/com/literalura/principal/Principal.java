package br.com.literalura.principal;

import br.com.literalura.model.*;
import br.com.literalura.repository.AutorRepository;
import br.com.literalura.repository.LivroRepository;
import br.com.literalura.service.ConsumoAPI;
import br.com.literalura.service.ConverteDados;

import java.util.ArrayList;
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
                     2-  Buscar livros
                     3 - Listar livros registrados
                     4 - Listar autores registrados
                     5 - Listar autores vivos em determinado ano
                     6 - Listar livros em um determinado idioma
                     7 - Sair       \s
                    \s""";

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {

                case 1:
                    buscarLivroPorTitulo();
                    break;
                case 2:
                    buscarLivroWeb();
                    break;
                case 3:
                    listarLivrosRegistrados();
                    break;
                case 4:
                    listarAutoresRegistrados();
                    break;
                case 5:
                    listarAutoresVivos();
                    break;
                case 6:
                    listarLivrosPorIdioma();
                    break;
                case 7:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção Inválida.");
                    break;
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
                System.out.println("Nenhum livro encontrado no banco de dados com esse idioma");
            }
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }


    private void listarAutoresVivos() {
        System.out.println("Listar autores vivos em determinado ano: ");
        var ano = leitura.nextInt();
        leitura.nextInt();

        List<Autor> autoresEncontrados = autorRepositorio.buscarAutoresVivosNoAno(ano);


        if (!autoresEncontrados.isEmpty()) {

            autoresEncontrados.forEach(a ->
                    System.out.printf("Autor: %s" +
                                    "Ano Nascimento: %d" +
                                    "Ano Falecimento: %s\n",
                            a.getNome(), a.getAnoNascimento(), a.getAnoFalecimentoFormatado()));
        } else {
            System.out.println("Nenhum autor encontrado vivo neste ano...");
        }
    }

    private void listarAutoresRegistrados() {
        autores = autorRepositorio.findAll();

        if (!autores.isEmpty()) {
            autores.forEach(System.out::println);
        } else {
            System.out.println("Nenhum autor registrado no banco");
        }
    }

    private void listarLivrosRegistrados() {
        livros = repositorio.findAll();

        livros.forEach(l -> System.out.printf(
                "Titulo: %s   -  Idioma: %s  -  Autor:  %s\n",
                l.getTitulo(), l.getIdioma(), l.getAutor()));
    }

    private void buscarLivroWeb() {
        DadosLivro dados = getDadosLivro();

        if (dados == null) {
            return;
        }

        Livro livro = new Livro(dados);

        if (dados.autor() != null && !dados.autor().isEmpty()) {
            DadosAutor dadosAutor = dados.autor().get(0); // Pega o primeiro autor
            Autor autor = new Autor();
            autor.setNome(dadosAutor.nomeAutor());

            try {
                autor.setAnoNascimento(Integer.parseInt(dadosAutor.anoNascimento()));
                autor.setAnoFalecimento(Integer.parseInt(dadosAutor.anoFalecimento()));
            } catch (NumberFormatException e) {
                System.out.println("Erro ao converter ano do autor.");
            }

            livro.setAutor(autor);
        }

        try {
            repositorio.save(livro);
            System.out.println("Livro e Autor salvos com sucesso: " + livro.getTitulo());
        } catch (Exception e) {
            System.out.println("Erro ao salvar o livro (pode já estar cadastrado): " + e.getMessage());
        }
    }

    private DadosLivro getDadosLivro() {
        System.out.println("Digite o nome do livro a ser buscado: ");
        var nomeLivro = leitura.nextLine();

        var json = consumo.obterDados(ENDERECO + "?search=" + nomeLivro.toLowerCase().replace(" ", "+"));
        DadosResposta dadosResposta = conversor.obterDados(json, DadosResposta.class);

        // Verifica se encontrou algum livro e retorna o primeiro (índice 0)
        if (dadosResposta.resultados() != null && !dadosResposta.resultados().isEmpty()) {
            return dadosResposta.resultados().get(0);
        } else {
            System.out.println("Nenhum livro encontrado.");
            return null;
        }
    }

    private void buscarLivroPorTitulo() {
        System.out.println("Digite o nome do livro a ser buscado: ");
        var nomeLivro = leitura.nextLine();

        livroBusca = repositorio.findByTituloIgnoreCase(nomeLivro);

        if (livroBusca.isPresent()) {
            System.out.println("Livro encontrado: " + livroBusca.get());
        } else {
            System.out.println("Nenhum livro encontrado com esse nome");
        }
    }
}
