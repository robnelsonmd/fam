package fam.puzzle.config;

import fam.puzzle.domain.PuzzleManager;
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
    PuzzleManager puzzleManager() {
        return new PuzzleManager();
    }
}
