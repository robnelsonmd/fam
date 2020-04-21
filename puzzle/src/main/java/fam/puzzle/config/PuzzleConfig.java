package fam.puzzle.config;

import fam.puzzle.generator.PuzzleGenerator;
import fam.puzzle.generator.PuzzleGeneratorImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class PuzzleConfig {
    @Bean
    File dataDirectory() {
        return new File("data");
    }

    @Bean
    PuzzleGenerator puzzleGenerator() {
        return new PuzzleGeneratorImpl();
    }
}
