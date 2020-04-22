package fam.puzzle.generator.hint;

import java.util.List;

public class OneNumberCorrectWellPlacedHint extends NumberSequenceHint {
    public static class Builder extends Hint.Builder<OneNumberCorrectWellPlacedHint, OneNumberCorrectWellPlacedHint.Builder> {
        public Builder(List<Integer> answer) {
            super(answer);
        }

        @Override
        protected OneNumberCorrectWellPlacedHint newHint() {
            return new OneNumberCorrectWellPlacedHint(this);
        }
    }

    protected OneNumberCorrectWellPlacedHint(Builder builder) {
        super(builder, 1, 1);
    }

    public static OneNumberCorrectWellPlacedHint.Builder builder(List<Integer> answer) {
        return new OneNumberCorrectWellPlacedHint.Builder(answer);
    }

    @Override
    protected List<Integer> generateHint(List<Integer> answer) {
        List<Integer> numberSequence = getInvalidNumberSequence(answer);
        int correctPosition = getRandomlyOrderedPositions().next();
        numberSequence.set(correctPosition, answer.get(correctPosition));
        return numberSequence;
    }

    @Override
    protected String getHintText() {
        return "One number is correct and well placed.";
    }
}
