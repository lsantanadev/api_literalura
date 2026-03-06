package br.com.literalura.principal;

import br.com.literalura.model.DadosLivro;
import br.com.literalura.service.ConsumoAPI;
import br.com.literalura.service.ConverteDados;

import java.util.Scanner;

public class Principal {

    private Scanner leitura = new Scanner(System.in);

    private ConsumoAPI consumo = new ConsumoAPI();

    private ConverteDados conversor = new ConverteDados();

    private static final String ENDERECO = "https://gutendex.com/books";

    public void exibeMenu() {
        var opcao = 0;

        while (opcao != 6) {
            String menu = """
                     Escolha o número da sua opção:\s
                    \s
                     1 - Buscar livro pelo título
                     2 - Listar livros registrados
                     3 - Listar autores registrados
                     4 - Listar autores vivos em determinado ano
                     5 - Listar livros em um determinado idioma
                     6 - Sair       \s
                    \s""";

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {

                case 1:
                    buscarLivroPorTitulo();
                    break;
                case 2:
                    listarLivrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresVivos();
                    break;
                case 5:
                    listarLivrosPorIdioma();
                    break;
                case 6:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção Inválida.");
                    break;
            }
        }
    }

    private DadosLivro getDadosLivro() {
        System.out.println("Digite o nome do livro a ser buscado: ");
        var nomeLivro = leitura.nextLine();

        var json = consumo.obterDados(ENDERECO + "?search=" + nomeLivro.toLowerCase().replace(" ", "+"));
        return conversor.obterDados(json, DadosLivro.class);
    }
}
