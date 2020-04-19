package fam.puzzle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class PuzzleConfig {
    @Bean
    File dataDirectory() {
        return new File("data");
    }
}
