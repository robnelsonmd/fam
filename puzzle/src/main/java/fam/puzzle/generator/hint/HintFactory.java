package fam.puzzle.generator.hint;

import java.util.List;

public final class HintFactory {
    public static Hint createHint(HintType hintType, List<Integer> answer) {
        switch (hintType) {
            case ALL_WRONG:
                return AllNumbersIncorrectHint.builder(answer).build();

            case ONE_RIGHT_CORRECT_POSITION:
                return NumberSequenceHint.builder(answer)
                        .numberOfCorrectDigits(1)
                        .numberOfCorrectPlacements(1)
                        .build();

            case ONE_RIGHT_INCORRECT_POSITION:
                return NumberSequenceHint.builder(answer)
                        .numberOfCorrectDigits(1)
                        .numberOfCorrectPlacements(0)
                        .build();

            case TWO_RIGHT_ONE_CORRECT_POSITION:
                return NumberSequenceHint.builder(answer)
                        .numberOfCorrectDigits(2)
                        .numberOfCorrectPlacements(1)
                        .build();

            case TWO_RIGHT_INCORRECT_POSITION:
                return NumberSequenceHint.builder(answer)
                        .numberOfCorrectDigits(2)
                        .numberOfCorrectPlacements(0)
                        .build();

            default:
                throw new IllegalArgumentException("Invalid Hint Type: " + hintType);
        }
    }
}
