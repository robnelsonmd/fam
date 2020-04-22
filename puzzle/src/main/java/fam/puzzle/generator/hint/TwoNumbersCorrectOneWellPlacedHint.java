package fam.puzzle.generator.hint;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

    @Override
    protected List<Integer> generateHint(List<Integer> answer) {
        List<Integer> numberSequence = getInvalidNumberSequence(answer);
        Iterator<Integer> positions = getRandomlyOrderedPositions();
        int position1 = positions.next();
        int position2 = positions.next();
        int position3 = positions.next();
        numberSequence.set(position1, answer.get(position1));
        numberSequence.set(position2, answer.get(position3));
        return numberSequence;
    }

    @Override
    protected String getHintText() {
        return "Two numbers are correct but only one is well placed.";
    }
}