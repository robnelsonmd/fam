package fam.puzzle.generator.hint;

import java.util.List;

public class OneNumberCorrectWronglyPlacedHint extends NumberSequenceHint {
    public static class Builder extends Hint.Builder<OneNumberCorrectWronglyPlacedHint, OneNumberCorrectWronglyPlacedHint.Builder> {
        public Builder(List<Integer> answer) {
            super(answer);
        }

        @Override
        protected OneNumberCorrectWronglyPlacedHint newHint() {
            return new OneNumberCorrectWronglyPlacedHint(this);
        }
    }

    protected OneNumberCorrectWronglyPlacedHint(Builder builder) {
        super(builder, 1, 0);
    }

    public static Builder builder(List<Integer> answer) {
        return new Builder(answer);
    }
}
