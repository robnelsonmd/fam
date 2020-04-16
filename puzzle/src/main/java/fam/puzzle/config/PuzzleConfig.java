package fam.puzzle.config;

import fam.puzzle.domain.PuzzleManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PuzzleConfig {
    @Bean
    PuzzleManager puzzleManager() {
        return new PuzzleManager();
    }
}
