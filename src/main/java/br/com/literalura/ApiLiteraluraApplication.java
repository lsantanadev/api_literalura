package br.com.literalura;

import br.com.literalura.principal.Principal;
import br.com.literalura.repository.AutorRepository;
import br.com.literalura.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiLiteraluraApplication implements CommandLineRunner {

    @Autowired
    private LivroRepository repositorio;

    @Autowired
    private AutorRepository autorRepositorio;

    public static void main(String[] args) {
        SpringApplication.run(ApiLiteraluraApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Principal principal = new Principal(repositorio, autorRepositorio);
        principal.exibeMenu();
    }
}
