package fam.puzzle.generator.hint;

import java.util.List;

public class AllNumbersIncorrectHint extends Hint {
    public static class Builder extends Hint.Builder<AllNumbersIncorrectHint,Builder> {
        public Builder(List<Integer> answer) {
            super(answer);
        }

        @Override
        protected AllNumbersIncorrectHint newHint() {
            return new AllNumbersIncorrectHint(this);
        }
    }

    protected AllNumbersIncorrectHint(Builder builder) {
        super(builder);
    }

    public static Builder builder(List<Integer> answer) {
        return new Builder(answer);
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
