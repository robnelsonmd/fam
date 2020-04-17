package fam.puzzle.service;

import fam.puzzle.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class PuzzleServiceImpl implements PuzzleService {
    private final Map<Player,Puzzle> puzzleMap = new HashMap<>();

    @Override
    public Puzzle generateNewPuzzle(Player player) {
        Puzzle puzzle;

        do {
            List<Integer> answer = generateAnswer();
            List<Hint> hints = generateHints(answer);
            puzzle = new Puzzle(answer, hints);
        } while (puzzle.getPossibleMatches().size() != 1);

        puzzleMap.put(player, puzzle);
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
