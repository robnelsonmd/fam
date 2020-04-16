package fam.puzzle.util;

import fam.puzzle.domain.Hint;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class PuzzleUtil {
    public static Set<List<Integer>> getAllPossibleNumberSequences() {
        List<List<Integer>> numberSequences = IntStream.range(0,1000)
                .mapToObj(PuzzleUtil::convertToNumberSequence)
                .collect(Collectors.toList());
        Collections.shuffle(numberSequences);
        return new HashSet<>(numberSequences);
    }

    public static List<Integer> convertToNumberSequence(int value) {
        return String.format("%03d",value)
                .chars()
                .map(Character::getNumericValue)
                .boxed()
                .collect(Collectors.toList());
    }
}
