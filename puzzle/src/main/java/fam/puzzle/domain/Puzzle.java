package fam.puzzle.domain;

import fam.puzzle.util.PuzzleUtil;

import java.util.List;
import java.util.Set;

public class Puzzle {
    private Integer incorrectGuessCount;
    private final List<Integer> answer;
    private final List<Hint> hints;
    private final Set<List<Integer>> possibleMatches;

    public Puzzle(List<Integer> answer, List<Hint> hints) {
        this.answer = answer;
        this.hints = hints;
        this.possibleMatches = findAllPossibleMatches(hints);
    }

    public List<Integer> getAnswer() {
        return answer;
    }

    public List<Hint> getHints() {
        return hints;
    }

    public Integer getIncorrectGuessCount() {
        return incorrectGuessCount;
    }

    public Set<List<Integer>> getPossibleMatches() {
        return possibleMatches;
    }

    public boolean isCorrectGuess(int guess) {
        boolean correctGuess = possibleMatches.contains(PuzzleUtil.convertToNumberSequence(guess));
        if (!correctGuess) incorrectGuessCount = (incorrectGuessCount == null) ? 1 : (incorrectGuessCount + 1);
        return correctGuess;
    }

    @Override
    public String toString() {
        return "Puzzle{" +
                "answer=" + answer +
                ", hints=" + hints +
                ", possibleMatches.size()=" + possibleMatches.size() +
                '}';
    }

    private static Set<List<Integer>> findAllPossibleMatches(
            List<Hint> hints) {
        Set<List<Integer>> possibleMatches = PuzzleUtil.getAllPossibleNumberSequences();
        hints.forEach(hint -> {
            possibleMatches.retainAll(hint.getPossibleMatches());
        });
        return possibleMatches;
    }
}
