package fam.puzzle.domain;

import fam.puzzle.generator.hint.Hint;
import fam.puzzle.util.PuzzleUtil;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AbstractPuzzle implements Puzzle {
    private final List<Hint> hints;
    private final List<Integer> answer;
    private final Set<List<Integer>> possibleMatches;

    private Integer incorrectGuessCount;

    public AbstractPuzzle(List<Integer> answer, List<Hint> hints, Set<List<Integer>> possibleMatches) {
        this.answer = answer;
        this.hints = hints;
        this.possibleMatches = possibleMatches;
    }

    @Override
    public List<Integer> getAnswer() {
        return answer;
    }

    @Override
    public List<Hint> getHints() {
        return hints;
    }

    @Override
    public Integer getIncorrectGuessCount() {
        return incorrectGuessCount;
    }

    @Override
    public Set<List<Integer>> getPossibleMatches() {
        return possibleMatches;
    }

    @Override
    public boolean isCorrectGuess(int guess) {
        boolean correctGuess = possibleMatches.contains(PuzzleUtil.convertToNumberSequence(guess));
        if (!correctGuess) incorrectGuessCount = (incorrectGuessCount == null) ? 1 : (incorrectGuessCount + 1);
        return correctGuess;
    }

    protected static List<Integer> generateAnswer(int size) {
        List<Integer> numbers = IntStream.range(0,10)
                .boxed()
                .collect(Collectors.toList());

        Collections.shuffle(numbers);
        return numbers.subList(0, size);
    }

    protected static Set<List<Integer>> findAllPossibleMatches(
            List<Hint> hints) {
        final Set<List<Integer>> possibleMatches = PuzzleUtil.getAllPossibleNumberSequences();
        hints.forEach(hint -> {
            possibleMatches.retainAll(hint.getPossibleMatches());
        });
        return possibleMatches;
    }
}
