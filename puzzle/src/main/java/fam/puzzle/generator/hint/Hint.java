package fam.puzzle.generator.hint;

import fam.puzzle.util.PuzzleUtil;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class Hint {
    public static abstract class Builder<H extends Hint, B extends Builder<H,B>> {
        private final List<Integer> answer = new ArrayList<>();
        private final H hint;

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

    public void initialize(List<Integer> answer) {
        this.hint = generateHint(answer);
        this.possibleMatches = generatePossibleMatches(hint);

        if (!this.getPossibleMatches().contains(answer)) {
            System.out.println("XXX");
            System.out.println(this);
            System.out.println("answer = " + answer);
            System.out.println("this.possibleMatches.size() = " + this.possibleMatches.size());
            this.possibleMatches.forEach(System.out::println);
            System.exit(1);
        }
    }

    public Set<List<Integer>> getPossibleMatches() {
        return possibleMatches;
    }

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

    protected abstract String getHintText();

    protected List<Integer> getInvalidNumberSequence(final List<Integer> answer) {
        List<Integer> numberList = IntStream.range(0,10)
                .filter(number -> !answer.contains(number))
                .boxed()
                .collect(Collectors.toList());

        Collections.shuffle(numberList);
        return numberList.subList(0,3);
    }

    protected Iterator<Integer> getRandomlyOrderedPositions() {
        List<Integer> positions = Arrays.asList(0,1,2);
        Collections.shuffle(positions);
        return positions.iterator();
    }

    protected boolean isSameLocation(List<Integer> sequence, List<Integer> hint, Integer number) {
        return sequence.contains(number)
                && hint.contains(number)
                && (sequence.indexOf(number) == hint.indexOf(number));
    }

    protected abstract boolean isPossibleMatch(List<Integer> sequence, List<Integer> hint);

    private Set<List<Integer>> generatePossibleMatches(final List<Integer> hint) {
        return PuzzleUtil.getAllPossibleNumberSequences().stream()
                .filter(sequence -> isPossibleMatch(sequence, hint))
                .collect(Collectors.toSet());
    }
}
