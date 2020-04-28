package fam.puzzle.generator.hint;

import java.util.Arrays;

public enum HintType {
    ALL_WRONG(0),
    NUMBER_SEQUENCE(1);

    private final int value;

    HintType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static HintType fromValue(int value) {
        return Arrays.stream(values())
                .filter(hintType -> (hintType.getValue() == value))
                .findFirst().orElse(null);
    }
}
