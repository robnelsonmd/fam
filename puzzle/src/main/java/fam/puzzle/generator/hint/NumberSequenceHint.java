package fam.puzzle.generator.hint;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class NumberSequenceHint extends Hint {
    private final int numberOfCorrectDigits;
    private final int numberOfCorrectPlacements;

    public NumberSequenceHint(Builder builder, int numberOfCorrectDigits, int numberOfCorrectPlacements) {
        super(builder);
        this.numberOfCorrectDigits = numberOfCorrectDigits;
        this.numberOfCorrectPlacements = numberOfCorrectPlacements;
    }

    @Override
    protected List<Integer> generateHint(List<Integer> answer) {
        List<Integer> numberSequence = getInvalidNumberSequence(answer);
        Iterator<Integer> positions = getRandomlyOrderedPositions(answer);
        correctlyPlaceDigitsInNumberSequence(answer, numberSequence, positions);
        incorrectlyPlaceDigitsInNumberSequence(answer, numberSequence, positions);

        return numberSequence;
    }

    protected Iterator<Integer> getRandomlyOrderedPositions(List<Integer> answer) {
        int numberOfPositions = numberOfCorrectDigits + (numberOfCorrectDigits - numberOfCorrectPlacements);
        List<Integer> positions = IntStream.range(0,answer.size()).boxed().limit(numberOfPositions).collect(Collectors.toList());
        Collections.shuffle(positions);
        return positions.iterator();
    }

    @Override
    protected boolean isPossibleMatch(List<Integer> sequence, List<Integer> hint) {
        Set<Integer> commonNumbers = getCommonNumbers(sequence, hint);

        return (commonNumbers.size() == numberOfCorrectDigits)
                && (commonNumbers.stream()
                .filter(number -> isSameLocation(sequence, hint, number))
                .count() == numberOfCorrectPlacements);
    }

    private void correctlyPlaceDigitsInNumberSequence(List<Integer> answer, List<Integer> numberSequence, Iterator<Integer> positions) {
        for (int i = 0; i < numberOfCorrectPlacements; i++) {
            int correctPosition = positions.next();
            numberSequence.set(correctPosition, answer.get(correctPosition));
        }
    }

    private void incorrectlyPlaceDigitsInNumberSequence(List<Integer> answer, List<Integer> numberSequence, Iterator<Integer> positions) {
        int numberOfIncorrectlyPlacedDigits = (numberOfCorrectDigits-numberOfCorrectPlacements);
        for (int i = 0; i < numberOfIncorrectlyPlacedDigits; i+=2) {
            int position1 = positions.next();
            int position2 = positions.next();

            numberSequence.set(position1, answer.get(position2));

            if (numberOfIncorrectlyPlacedDigits > 1) {
                numberSequence.set(position2, answer.get(position1));
            }
        }
    }
}
