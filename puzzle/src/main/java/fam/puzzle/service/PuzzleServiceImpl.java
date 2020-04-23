package fam.puzzle.service;

import fam.puzzle.domain.Puzzle;
import fam.puzzle.generator.PuzzleGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class PuzzleServiceImpl implements PuzzleService {
    private static final Logger LOG = LoggerFactory.getLogger(PuzzleServiceImpl.class);
    private static final int MIN_CACHE_SIZE = 25;

    private final List<Puzzle> fourDigitPuzzleCache = new CopyOnWriteArrayList<>();
    private final List<Puzzle> threeDigitPuzzleCache = new CopyOnWriteArrayList<>();
    private final Environment environment;
    private final PuzzleGenerator puzzleGenerator;
    private final ScheduledExecutorService threeDigitPuzzleGeneratorService = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService fourDigitPuzzleGeneratorService = Executors.newScheduledThreadPool(3);

    public PuzzleServiceImpl(Environment environment, PuzzleGenerator puzzleGenerator) {
        this.environment = environment;
        this.puzzleGenerator = puzzleGenerator;
    }

    @Override
    public Puzzle generateNewPuzzle(int size) {
        return getPuzzle(size);
    }

    @PostConstruct
    public void initializePuzzleGenerators() {
        boolean devProfileActive = Arrays.asList(environment.getActiveProfiles()).contains("dev");
        final int minCacheSize = !devProfileActive ? MIN_CACHE_SIZE : 5;
        threeDigitPuzzleGeneratorService.scheduleWithFixedDelay(() -> generateThreeDigitPuzzles(minCacheSize), 0, 1, TimeUnit.SECONDS);
        fourDigitPuzzleGeneratorService.scheduleWithFixedDelay(() -> generateFourDigitPuzzles(minCacheSize), 0, 1, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void shutdownPuzzleGenerators() {
        threeDigitPuzzleGeneratorService.shutdownNow();
        fourDigitPuzzleGeneratorService.shutdownNow();
    }

    private void generateFourDigitPuzzles(int minSize) {
        if (fourDigitPuzzleCache.size() < minSize) {
            LOG.info("Generating four digit puzzles...");
            for (int i = 0; i < (minSize * 2); i++) {
                fourDigitPuzzleGeneratorService.execute(() -> fourDigitPuzzleCache.add(puzzleGenerator.generatePuzzle(4)));
            }
        }
    }

    private void generateThreeDigitPuzzles(int minSize) {
        if (threeDigitPuzzleCache.size() < minSize) {
            LOG.info("Generating three digit puzzles...");
            for (int i = 0; i < (minSize * 2); i++) {
                threeDigitPuzzleCache.add(puzzleGenerator.generatePuzzle(3));
            }
        }
    }

    private Puzzle getPuzzle(int size) {
        switch (size) {
            case 3:
                return !threeDigitPuzzleCache.isEmpty() ?
                        threeDigitPuzzleCache.remove(0) :
                        puzzleGenerator.generatePuzzle(3);

            case 4:
                return !fourDigitPuzzleCache.isEmpty() ?
                        fourDigitPuzzleCache.remove(0) :
                        puzzleGenerator.generatePuzzle(4);

            default:
                throw new IllegalArgumentException("Invalid puzzle size: " + size);
        }
    }
}
