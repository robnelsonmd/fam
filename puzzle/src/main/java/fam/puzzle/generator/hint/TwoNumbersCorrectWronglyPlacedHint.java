package fam.puzzle.generator.hint;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
    protected List<Integer> generateHint(List<Integer> answer) {
        List<Integer> numberSequence = getInvalidNumberSequence(answer);
        Iterator<Integer> positions = getRandomlyOrderedPositions();
        int position1 = positions.next();
        int position2 = positions.next();
        numberSequence.set(position1, answer.get(position2));
        numberSequence.set(position2, answer.get(position1));
        return numberSequence;
    }

    @Override
    protected String getHintText() {
        return "Two numbers are correct but wrongly placed.";
    }
}
