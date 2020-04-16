package fam.puzzle.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PuzzleManager {
    private Map<String,Puzzle> puzzleMap = new HashMap<>();

    public Puzzle generateNewPuzzle(String name) {
        Puzzle puzzle;

        do {
            List<Integer> answer = generateAnswer();
            List<Hint> hints = generateHints(answer);
            puzzle = new Puzzle(answer, hints);
        } while (puzzle.getPossibleMatches().size() != 1);

        puzzleMap.put(name, puzzle);
        return puzzle;
    }

    public Puzzle getPuzzle(String name) {
        return puzzleMap.get(name);
    }

    private static List<Integer> generateAnswer() {
        List<Integer> numbers = IntStream.range(0,10)
                .boxed()
                .collect(Collectors.toList());

        Collections.shuffle(numbers);
        return numbers.subList(0,3);
    }

    private static List<Hint> generateHints(List<Integer> answer) {
        List<Hint> hints = Arrays.asList(
            HintFactory.createHint(HintType.ONE_RIGHT_CORRECT_POSITION, answer),
            HintFactory.createHint(HintType.ALL_WRONG, answer),
            HintFactory.createHint(HintType.ONE_RIGHT_INCORRECT_POSITION, answer),
            HintFactory.createHint(HintType.TWO_RIGHT_INCORRECT_POSITION, answer),
            HintFactory.createHint(HintType.TWO_RIGHT_ONE_CORRECT_POSITION, answer)
        );

        Collections.shuffle(hints);
        return hints;
    }
}
