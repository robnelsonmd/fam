package fam.puzzle.generator.hint;

import java.util.List;
import java.util.Set;

public abstract class NumberSequenceHint extends Hint {
    private final int numberOfCorrectDigits;
    private final int numberOfCorrectPlacements;

    public NumberSequenceHint(Builder builder, int numberOfCorrectDigits, int numberOfCorrectPlacements) {
        super(builder);
        this.numberOfCorrectDigits = numberOfCorrectDigits;
        this.numberOfCorrectPlacements = numberOfCorrectPlacements;
    }

    @Override
    protected boolean isPossibleMatch(List<Integer> sequence, List<Integer> hint) {
        Set<Integer> commonNumbers = getCommonNumbers(sequence, hint);

        return (commonNumbers.size() == numberOfCorrectDigits)
                && (commonNumbers.stream()
                .filter(number -> isSameLocation(sequence, hint, number))
                .count() == numberOfCorrectPlacements);
    }
}
