package fam.puzzle.domain;

import fam.puzzle.generator.hint.Hint;
import fam.puzzle.generator.hint.HintFactory;
import fam.puzzle.generator.hint.HintType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ThreeDigitPuzzle extends AbstractPuzzle {
    private ThreeDigitPuzzle(List<Integer> answer, List<Hint> hints, Set<List<Integer>> possibleMatches) {
        super(answer, hints, possibleMatches);
    }

    public static ThreeDigitPuzzle newInstance() {
        List<Integer> answer;
        List<Hint> hints;
        Set<List<Integer>> possibleMatches;

        do {
            answer = generateAnswer();
            hints = generateHints(answer);
            possibleMatches = findAllPossibleMatches(hints);
        } while (possibleMatches.size() > 1);

        return new ThreeDigitPuzzle(answer, hints, possibleMatches);
    }

    private static List<Integer> generateAnswer() {
        return generateAnswer(3);
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
