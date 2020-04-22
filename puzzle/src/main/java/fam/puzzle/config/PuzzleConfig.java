package fam.puzzle.config;

import fam.puzzle.generator.PuzzleGenerator;
import fam.puzzle.generator.PuzzleGeneratorImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class PuzzleConfig {
    @Bean
    public String applicationVersion(@Value("${application.version}") String applicationVersion) {
        return applicationVersion;
    }

    @Bean
    public File dataDirectory() {
        return new File("data");
    }

    @Bean
    public PuzzleGenerator puzzleGenerator() {
        return new PuzzleGeneratorImpl();
    }
}
