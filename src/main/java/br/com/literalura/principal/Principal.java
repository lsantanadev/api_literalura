package br.com.literalura.demo.principal;

import br.com.literalura.demo.service.ConsumoAPI;
import br.com.literalura.demo.service.ConverteDados;

import java.util.Scanner;

public class Principal {

    private Scanner leitura = new Scanner(System.in);

    private ConsumoAPI consumo = new ConsumoAPI();

    private ConverteDados conversor = new ConverteDados();

    private static final String ENDERECO = "https://gutendex.com/books";

    public void exibeMenu() {

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
        var opcao = leitura.nextInt();
        leitura.nextLine();

        while (opcao != 6) {
            switch (opcao) {
                
            }
        }

    }
}
