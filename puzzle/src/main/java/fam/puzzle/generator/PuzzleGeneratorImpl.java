package fam.puzzle.generator;

import fam.puzzle.domain.Puzzle;
import fam.puzzle.generator.hint.Hint;
import fam.puzzle.generator.hint.HintFactory;
import fam.puzzle.generator.hint.HintType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PuzzleGeneratorImpl implements PuzzleGenerator {
    @Override
    public Puzzle generateNewPuzzle() {
        Puzzle puzzle;

        do {
            List<Integer> answer = generateAnswer();
            List<Hint> hints = generateHints(answer);
            puzzle = new Puzzle(answer, hints);
        } while (puzzle.getPossibleMatches().size() != 1);

        return puzzle;
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
