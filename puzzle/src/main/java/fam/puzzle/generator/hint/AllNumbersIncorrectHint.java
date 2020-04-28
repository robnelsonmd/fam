package fam.puzzle.generator.hint;

import fam.puzzle.util.PuzzleUtil;

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

    private AllNumbersIncorrectHint(List<Integer> hint) {
        setHint(hint);
    }

    public static Builder builder(List<Integer> answer) {
        return new Builder(answer);
    }

    public static AllNumbersIncorrectHint deserialize(String hintString) {
        List<Integer> hint = PuzzleUtil.deserializeNumberList(hintString);
        return new AllNumbersIncorrectHint(hint);
    }

    @Override
    public String serialize() {
        return String.format("%d:%s",HintType.ALL_WRONG.getValue(), PuzzleUtil.serializeNumberList(getHint()));
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
