package fam.puzzle.domain;

import fam.puzzle.generator.hint.Hint;
import fam.puzzle.util.PuzzleUtil;

import java.util.List;
import java.util.Set;

public class Puzzle {
    private final List<Hint> hints;
    private final List<Integer> answer;
    private final Set<List<Integer>> possibleMatches;

    private Integer incorrectGuessCount;

    public Puzzle(List<Integer> answer, List<Hint> hints, Set<List<Integer>> possibleMatches) {
        this.answer = answer;
        this.hints = hints;
        this.possibleMatches = possibleMatches;
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
}
