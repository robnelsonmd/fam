package fam.puzzle.generator.hint;

import java.util.List;

public class TwoNumbersCorrectOneWellPlacedHint extends NumberSequenceHint {
    public static class Builder extends Hint.Builder<TwoNumbersCorrectOneWellPlacedHint, TwoNumbersCorrectOneWellPlacedHint.Builder> {
        public Builder(List<Integer> answer) {
            super(answer);
        }

        @Override
        protected TwoNumbersCorrectOneWellPlacedHint newHint() {
            return new TwoNumbersCorrectOneWellPlacedHint(this);
        }
    }

    protected TwoNumbersCorrectOneWellPlacedHint(Builder builder) {
        super(builder, 2, 1);
    }

    public static Builder builder(List<Integer> answer) {
        return new Builder(answer);
    }
}