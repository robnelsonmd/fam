package fam.puzzle.generator.hint;

import java.util.List;

public final class HintFactory {
    public static Hint createHint(HintType hintType, List<Integer> answer) {
        switch (hintType) {
            case ALL_WRONG:
                return AllNumbersIncorrectHint.builder(answer).build();

            case ONE_RIGHT_CORRECT_POSITION:
                return OneNumberCorrectWellPlacedHint.builder(answer).build();

            case ONE_RIGHT_INCORRECT_POSITION:
                return OneNumberCorrectWronglyPlacedHint.builder(answer).build();

            case TWO_RIGHT_ONE_CORRECT_POSITION:
                return TwoNumbersCorrectOneWellPlacedHint.builder(answer).build();

            case TWO_RIGHT_INCORRECT_POSITION:
                return TwoNumbersCorrectWronglyPlacedHint.builder(answer).build();

            default:
                throw new IllegalArgumentException("Invalid Hint Type: " + hintType);
        }
    }
}
