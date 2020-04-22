package fam.puzzle.generator.hint;

import java.util.List;
import java.util.Set;

public class OneNumberCorrectWellPlacedHint extends Hint {
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
        super(builder);
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

    @Override
    protected boolean isPossibleMatch(List<Integer> sequence, List<Integer> hint) {
        Set<Integer> commonNumbers = getCommonNumbers(sequence, hint);

        return (commonNumbers.size() == 1)
                && isSameLocation(sequence, hint, commonNumbers.iterator().next());
    }
}
