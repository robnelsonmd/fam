package fam.puzzle.generator.hint;

import java.util.List;

public class TwoNumbersCorrectWronglyPlacedHint extends NumberSequenceHint {
    public static class Builder extends Hint.Builder<TwoNumbersCorrectWronglyPlacedHint, TwoNumbersCorrectWronglyPlacedHint.Builder> {
        public Builder(List<Integer> answer) {
            super(answer);
        }

        @Override
        protected TwoNumbersCorrectWronglyPlacedHint newHint() {
            return new TwoNumbersCorrectWronglyPlacedHint(this);
        }
    }

    protected TwoNumbersCorrectWronglyPlacedHint(Builder builder) {
        super(builder, 2, 0);
    }

    public static Builder builder(List<Integer> answer) {
        return new Builder(answer);
    }

    @Override
    protected String getHintText() {
        return "Two numbers are correct but wrongly placed.";
    }
}
