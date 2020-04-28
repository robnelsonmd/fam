package fam.puzzle.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class PuzzleUtil {
    public static List<Integer> convertToNumberSequence(int value, int numberOfDigits) {
        return getFormattedNumberString(value,numberOfDigits)
                .chars()
                .map(Character::getNumericValue)
                .boxed()
                .collect(Collectors.toList());
    }

    public static Set<List<Integer>> getAllPossibleNumberSequences(final int numberOfDigits) {
        int numberSequenceRangeUpperLimit = (int) Math.pow(10, numberOfDigits);
        List<List<Integer>> numberSequences = IntStream.range(0,numberSequenceRangeUpperLimit)
                .mapToObj(value -> convertToNumberSequence(value, numberOfDigits))
                .collect(Collectors.toList());
        Collections.shuffle(numberSequences);
        return new HashSet<>(numberSequences);
    }

    public static String getFormattedNumberString(int value, int numberOfDigits) {
        String numberFormatString = String.format("%%0%dd",numberOfDigits);
        return String.format(numberFormatString, value);
    }

    public static String getPuzzleSizeString(int size) {
        switch(size) {
            case 3:
                return "Three";
            case 4:
                return "Four";
            case 5:
                return "Five";
            case 6:
                return "Six";
            default:
                throw new IllegalStateException("Invalid size: " + size);
        }
    }

    public static List<Integer> deserializeNumberList(String numberList) {
        return numberList.chars().map(Character::getNumericValue).boxed().collect(Collectors.toList());
    }

    public static String serializeNumberList(List<Integer> numbers) {
        return numbers.stream().map(String::valueOf).collect(Collectors.joining(""));
    }
}
