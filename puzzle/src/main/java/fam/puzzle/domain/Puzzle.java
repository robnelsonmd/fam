package fam.puzzle.domain;

import fam.puzzle.generator.hint.Hint;

import java.util.List;
import java.util.Set;

public interface Puzzle {
    List<Integer> getAnswer();
    List<Hint> getHints();
    Integer getIncorrectGuessCount();
    Set<List<Integer>> getPossibleMatches();
    boolean isCorrectGuess(int guess);
}
