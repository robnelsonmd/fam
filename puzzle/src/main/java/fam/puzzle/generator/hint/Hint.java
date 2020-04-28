package fam.puzzle.generator.hint;

import fam.puzzle.util.PuzzleUtil;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class Hint implements Serializable {
    public static abstract class Builder<H extends Hint, B extends Builder<H,B>> {
        private final List<Integer> answer = new ArrayList<>();
        protected final H hint;

        public Builder(List<Integer> answer) {
            this.answer.addAll(answer);
            this.hint = newHint();
        }

        public H build() {
            hint.initialize(answer);

            return hint;
        }

        protected abstract H newHint();
    }

    private List<Integer> hint;
    private Set<List<Integer>> possibleMatches;

    protected Hint(Builder builder) {
    }

    protected Hint() {}

    public void initialize(List<Integer> answer) {
        this.hint = generateHint(answer);
        this.possibleMatches = generatePossibleMatches(hint, answer.size());

        if (!this.getPossibleMatches().contains(answer)) {
            System.out.println("XXX");
            System.out.println(this);
            System.out.println("answer = " + answer);
            System.out.println("this.possibleMatches.size() = " + this.possibleMatches.size());
            System.exit(1);
        }
    }

    public Set<List<Integer>> getPossibleMatches() {
        return possibleMatches;
    }

    public abstract String serialize();

    @Override
    public String toString() {
        return String.format("%s: %s",hint, getHintText());
    }

    protected abstract List<Integer> generateHint(List<Integer> answer);

    protected Set<Integer> getCommonNumbers(List<Integer> sequence, List<Integer> hint) {
        Set<Integer> matchingNumbers = new HashSet<>(sequence);
        matchingNumbers.retainAll(hint);
        return matchingNumbers;
    }

    protected List<Integer> getHint() {
        return hint;
    }

    protected abstract String getHintText();

    protected List<Integer> getInvalidNumberSequence(final List<Integer> answer) {
        List<Integer> numberList = IntStream.range(0,10)
                .filter(number -> !answer.contains(number))
                .boxed()
                .collect(Collectors.toList());

        Collections.shuffle(numberList);
        return numberList.subList(0,answer.size());
    }

    protected boolean isSameLocation(List<Integer> sequence, List<Integer> hint, Integer number) {
        return sequence.contains(number)
                && (sequence.indexOf(number) == hint.indexOf(number));
    }

    protected abstract boolean isPossibleMatch(List<Integer> sequence, List<Integer> hint);

    protected void setHint(List<Integer> hint) {
        this.hint = hint;
        this.possibleMatches = generatePossibleMatches(hint, hint.size());
    }

    private Set<List<Integer>> generatePossibleMatches(final List<Integer> hint, int numberOfDigits) {
        return PuzzleUtil.getAllPossibleNumberSequences(numberOfDigits).stream()
                .filter(sequence -> isPossibleMatch(sequence, hint))
                .collect(Collectors.toSet());
    }
}
