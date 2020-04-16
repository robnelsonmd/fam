package fam.puzzle.domain;

import java.util.List;

public final class HintFactory {
    public static Hint createHint(HintType hintType, List<Integer> answer) {
        switch (hintType) {
            case ALL_WRONG:
                return new AllNumbersIncorrectHint(answer);

            case ONE_RIGHT_CORRECT_POSITION:
                return new OneNumberCorrectWellPlacedHint(answer);

            case ONE_RIGHT_INCORRECT_POSITION:
                return new OneNumberCorrectWronglyPlacedHint(answer);

            case TWO_RIGHT_ONE_CORRECT_POSITION:
                return new TwoNumbersCorrectOneWellPlacedHint(answer);

            case TWO_RIGHT_INCORRECT_POSITION:
                return new TwoNumbersCorrectWronglyPlacedHint(answer);

            default:
                throw new IllegalArgumentException("Invalid Hint Type: " + hintType);
        }
    }
}
