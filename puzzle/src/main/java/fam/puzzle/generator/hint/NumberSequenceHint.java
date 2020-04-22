package fam.puzzle.generator.hint;

import fam.core.util.StringUtil;

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

    @Override
    protected String getHintText() {
        return String.format("%s %s",getHintStringPrefix(),getHintStringSuffix());
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

    private String getHintStringPrefix() {
        String numberString = StringUtil.capitalizeString(getNumberString(numberOfCorrectDigits), StringUtil.CapitalizationStrategy.FIRST_LETTER);
        return String.format("%s correct",numberString);
    }

    private String getHintStringSuffix() {
        if (numberOfCorrectPlacements == 0) {
            return "but wrongly placed";
        }

        if (numberOfCorrectPlacements == numberOfCorrectDigits) {
            return "and well placed";
        }

        return String.format("but only %s well placed",getNumberString(numberOfCorrectPlacements));
    }

    private String getNumberString(int number) {
        switch (number) {
            case 1:
                return "one number is";
            case 2:
                return "two numbers are";
            case 3:
                return "three numbers are";
            case 4:
                return "four numbers are";
            case 5:
                return "five numbers are";
            case 6:
                return "six numbers are";
            case 7:
                return "seven numbers are";
            case 8:
                return "eight numbers are";
            case 9:
                return "nine numbers are";
            default:
                throw new IllegalArgumentException("Invalid number of digits specified: " + number);
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
