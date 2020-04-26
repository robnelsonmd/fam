package fam.puzzle.config;

import fam.core.executor.TaskScheduler;
import fam.core.executor.TaskSchedulerImpl;
import fam.puzzle.generator.PuzzleGenerator;
import fam.puzzle.generator.PuzzleGeneratorImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

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
    @Profile("dev")
    public PuzzleGenerator fourDigitPuzzeGeneratorDev() {
        return new PuzzleGeneratorImpl(4, 20);
    }

    @Bean
    @Profile("prod")
    public PuzzleGenerator fourDigitPuzzeGeneratorProd() {
        return new PuzzleGeneratorImpl(4, 50);
    }

    @Bean
    @Profile("dev")
    public PuzzleGenerator threeDigitPuzzeGeneratorDev() {
        return new PuzzleGeneratorImpl(3, 20);
    }

    @Bean
    @Profile("prod")
    public PuzzleGenerator threeDigitPuzzeGeneratorProd() {
        return new PuzzleGeneratorImpl(3, 50);
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new TaskSchedulerImpl(5);
    }
}
