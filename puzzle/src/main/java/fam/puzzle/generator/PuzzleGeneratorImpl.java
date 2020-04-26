package fam.puzzle.generator;

import fam.puzzle.domain.Puzzle;
import fam.puzzle.generator.hint.Hint;
import fam.puzzle.generator.hint.HintFactory;
import fam.puzzle.generator.hint.HintType;
import fam.puzzle.util.PuzzleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PuzzleGeneratorImpl implements PuzzleGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(PuzzleGeneratorImpl.class);

    private final List<Puzzle> puzzleCache = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final int cacheSize;
    private final int minCacheSize;
    private final int puzzleSize;

    public PuzzleGeneratorImpl(int puzzleSize, int cacheSize) {
        this.cacheSize = cacheSize;
        this.minCacheSize = cacheSize / 2;
        this.puzzleSize = puzzleSize;
    }

    @Override
    public Puzzle getPuzzle() {
        return !puzzleCache.isEmpty() ? puzzleCache.remove(0) : generatePuzzle();
    }

    @Override
    public int getPuzzleSize() {
        return puzzleSize;
    }

    @PostConstruct
    public void initialize() {
        executor.scheduleWithFixedDelay(this::generatePuzzles, 0, 1, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void shutdownPuzzleGenerators() {
        executor.shutdownNow();
    }

    private static Set<List<Integer>> findAllPossibleMatches(
            List<Hint> hints,
            int numberOfDigits
    ) {
        final Set<List<Integer>> possibleMatches = PuzzleUtil.getAllPossibleNumberSequences(numberOfDigits);
        hints.forEach(hint -> possibleMatches.retainAll(hint.getPossibleMatches()));
        return possibleMatches;
    }

    protected static List<Integer> generateAnswer(int size) {
        List<Integer> numbers = IntStream.range(0,10)
                .boxed()
                .collect(Collectors.toList());

        Collections.shuffle(numbers);
        return numbers.subList(0, size);
    }

    private static List<Hint> generateHints(List<Integer> answer) {
        List<Hint> hints = generateHintsForSize(answer, answer.size());
        Collections.shuffle(hints);
        return hints;
    }

    private static List<Hint> generateHintsForSize(List<Integer> answer, int size) {
        List<Hint> hints = (size == 3) ?
                Arrays.asList(
                    HintFactory.createHint(HintType.ONE_RIGHT_CORRECT_POSITION, answer),
                    HintFactory.createHint(HintType.ONE_RIGHT_CORRECT_POSITION, answer),
                    HintFactory.createHint(HintType.ONE_RIGHT_INCORRECT_POSITION, answer),
                    HintFactory.createHint(HintType.ONE_RIGHT_INCORRECT_POSITION, answer),
                    HintFactory.createHint(HintType.TWO_RIGHT_ONE_CORRECT_POSITION, answer)
                ) :
                Arrays.asList(
                    HintFactory.createHint(HintType.ONE_RIGHT_CORRECT_POSITION, answer),
                    HintFactory.createHint(HintType.ONE_RIGHT_CORRECT_POSITION, answer),
                    HintFactory.createHint(HintType.ONE_RIGHT_INCORRECT_POSITION, answer),
                    HintFactory.createHint(HintType.ONE_RIGHT_INCORRECT_POSITION, answer),
                    HintFactory.createHint(HintType.ONE_RIGHT_INCORRECT_POSITION, answer),
                    HintFactory.createHint(HintType.TWO_RIGHT_INCORRECT_POSITION, answer),
                    HintFactory.createHint(HintType.TWO_RIGHT_ONE_CORRECT_POSITION, answer)
                );

        return hints;
    }

    private Puzzle generatePuzzle() {
        List<Integer> answer;
        List<Hint> hints;
        Set<List<Integer>> possibleMatches;

        int count = 0;
        do {
            answer = generateAnswer(puzzleSize);
            hints = generateHints(answer);
            possibleMatches = findAllPossibleMatches(hints, answer.size());
        } while ((possibleMatches.size() > 1) && (++count < 1000));

        if (count >= 1000) {
            LOG.error("Failed to generate puzzle after 1000 attempts");
            System.exit(1);
        }

        if (count >= 10) {
            LOG.info(String.format("Required %d attempts to generate puzzle",count));
        }

        return new Puzzle(answer, hints, possibleMatches);
    }

    private void generatePuzzles() {
        if (puzzleCache.size() < minCacheSize) {
            LOG.info(String.format("Generating %d %s digit puzzles...",cacheSize,PuzzleUtil.getPuzzleSizeString(puzzleSize).toLowerCase()));
            for (int i = 0; i < cacheSize; i++) {
                puzzleCache.add(generatePuzzle());
            }
        }
    }
}
