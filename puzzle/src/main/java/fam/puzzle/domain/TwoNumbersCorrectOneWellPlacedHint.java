package fam.puzzle.domain;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TwoNumbersCorrectOneWellPlacedHint extends Hint {
    public TwoNumbersCorrectOneWellPlacedHint(List<Integer> answer) {
        super(answer);
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

    @Override
    protected boolean isPossibleMatch(List<Integer> sequence, List<Integer> hint) {
        Set<Integer> commonNumbers = getCommonNumbers(sequence, hint);

        return (commonNumbers.size() == 2)
                && (commonNumbers.stream()
                .filter(number -> isSameLocation(sequence, hint, number))
                .count() == 1);
    }
}