package fam.puzzle.service;

import fam.puzzle.domain.Player;
import fam.puzzle.domain.Puzzle;
import fam.puzzle.generator.PuzzleGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class PuzzleServiceImpl implements PuzzleService {
    private static final Logger LOG = LoggerFactory.getLogger(PuzzleServiceImpl.class);

    private final List<Puzzle> threeDigitPuzzles = new CopyOnWriteArrayList<>();
    private final List<Puzzle> fourDigitPuzzles = new CopyOnWriteArrayList<>();
    private final Map<Player,Puzzle> puzzleMap = new HashMap<>();
    private final PuzzleGenerator puzzleGenerator;
    private final ScheduledExecutorService threeDigitPuzzleGeneratorService = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService fourDigitPuzzleGeneratorService = Executors.newScheduledThreadPool(3);

    public PuzzleServiceImpl(PuzzleGenerator puzzleGenerator) {
        this.puzzleGenerator = puzzleGenerator;
    }

    @Override
    public Puzzle generateNewPuzzle(Player player, int size) {
        Puzzle puzzle = getPuzzle(size);
        puzzleMap.put(player, puzzle);
        return puzzle;
    }

    @PostConstruct
    public void initializePuzzleGenerators() {
        threeDigitPuzzleGeneratorService.scheduleWithFixedDelay(this::generateThreeDigitPuzzles, 0, 1, TimeUnit.SECONDS);
        fourDigitPuzzleGeneratorService.scheduleWithFixedDelay(this::generateFourDigitPuzzles, 0, 1, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void shutdownPuzzleGenerators() {
        threeDigitPuzzleGeneratorService.shutdownNow();
        fourDigitPuzzleGeneratorService.shutdownNow();
    }

    private void generateFourDigitPuzzles() {
        if (fourDigitPuzzles.size() < 25) {
            LOG.info("Generating four digit puzzles...");
            for (int i = 0; i < 50; i++) {
                fourDigitPuzzleGeneratorService.execute(() -> fourDigitPuzzles.add(puzzleGenerator.generatePuzzle(4)));
            }
        }
    }

    private void generateThreeDigitPuzzles() {
        if (threeDigitPuzzles.size() < 25) {
            LOG.info("Generating three digit puzzles...");
            for (int i = 0; i < 50; i++) {
                threeDigitPuzzles.add(puzzleGenerator.generatePuzzle(3));
            }
        }
    }

    private Puzzle getPuzzle(int size) {
        switch (size) {
            case 3:
                return !threeDigitPuzzles.isEmpty() ?
                        threeDigitPuzzles.remove(0) :
                        puzzleGenerator.generatePuzzle(3);

            case 4:
                return !fourDigitPuzzles.isEmpty() ?
                        fourDigitPuzzles.remove(0) :
                        puzzleGenerator.generatePuzzle(4);

            default:
                throw new IllegalArgumentException("Invalid puzzle size: " + size);
        }
    }
}
