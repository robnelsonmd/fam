package fam.puzzle.generator;

import fam.puzzle.domain.Puzzle;
import fam.puzzle.generator.hint.Hint;
import fam.puzzle.generator.hint.HintFactory;
import fam.puzzle.generator.hint.HintType;
import fam.puzzle.util.PuzzleUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PuzzleGeneratorImpl implements PuzzleGenerator {
    @Override
    public Puzzle generatePuzzle(int size) {
        List<Integer> answer;
        List<Hint> hints;
        Set<List<Integer>> possibleMatches;

        do {
            answer = generateAnswer(size);
            hints = generateHints(answer);
            possibleMatches = findAllPossibleMatches(hints);
        } while (possibleMatches.size() > 1);

        return new Puzzle(answer, hints, possibleMatches);
    }

    protected static List<Integer> generateAnswer(int size) {
        List<Integer> numbers = IntStream.range(0,10)
                .boxed()
                .collect(Collectors.toList());

        Collections.shuffle(numbers);
        return numbers.subList(0, size);
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

    private static Set<List<Integer>> findAllPossibleMatches(
            List<Hint> hints) {
        final Set<List<Integer>> possibleMatches = PuzzleUtil.getAllPossibleNumberSequences();
        hints.forEach(hint -> possibleMatches.retainAll(hint.getPossibleMatches()));
        return possibleMatches;
    }
}
