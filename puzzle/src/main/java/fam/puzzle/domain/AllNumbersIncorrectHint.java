package fam.puzzle.domain;

import java.util.List;

public class AllNumbersIncorrectHint extends Hint {
    public AllNumbersIncorrectHint(List<Integer> answer) {
        super(answer);
    }

    @Override
    protected List<Integer> generateHint(List<Integer> answer) {
        return getInvalidNumberSequence(answer);
    }

    @Override
    protected String getHintText() {
        return "All numbers are incorrect.";
    }

    @Override
    protected boolean isPossibleMatch(List<Integer> sequence, List<Integer> hint) {
        return getCommonNumbers(sequence, hint).isEmpty();
    }
}
