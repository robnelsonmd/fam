package fam.puzzle.domain;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class OneNumberCorrectWronglyPlacedHint extends Hint {
    public OneNumberCorrectWronglyPlacedHint(List<Integer> answer) {
        super(answer);
    }

    @Override
    protected List<Integer> generateHint(List<Integer> answer) {
        List<Integer> numberSequence = getInvalidNumberSequence(answer);
        Iterator<Integer> positions = getRandomlyOrderedPositions();
        int correctPosition = positions.next();
        int incorrectPosition = positions.next();
        numberSequence.set(incorrectPosition, answer.get(correctPosition));
        return numberSequence;
    }

    @Override
    protected String getHintText() {
        return "One number is correct but wrongly placed.";
    }

    @Override
    protected boolean isPossibleMatch(List<Integer> sequence, List<Integer> hint) {
        Set<Integer> commonNumbers = getCommonNumbers(sequence, hint);

        return (commonNumbers.size() == 1)
                && !isSameLocation(sequence, hint, commonNumbers.iterator().next());
    }
}
