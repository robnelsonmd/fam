package fam.puzzle.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fam.core.util.StringUtil;

import java.util.Objects;

public class Player {
    private final String name;
    private final int cheatCount;
    private final int correctGuessCount;
    private final int incorrectGuessCount;
    private final int showAnswerCount;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Player(
            @JsonProperty("name") String name,
            @JsonProperty("cheatCount") int cheatCount,
            @JsonProperty("correctGuessCount") int correctGuessCount,
            @JsonProperty("incorrectGuessCount") int incorrectGuessCount,
            @JsonProperty("showAnswerCount") int showAnswerCount
    ) {
        if (StringUtil.isEmptyString(name)) {
            throw new IllegalArgumentException(String.format("Invalid player name (%s)",name));
        }

        this.name = name;
        this.cheatCount = cheatCount;
        this.correctGuessCount = correctGuessCount;
        this.incorrectGuessCount = incorrectGuessCount;
        this.showAnswerCount = showAnswerCount;
    }

    public Player(String name) {
        this(name, 0, 0, 0, 0);
    }

    public String getName() {
        return name;
    }

    public int getCheatCount() {
        return cheatCount;
    }

    public int getCorrectGuessCount() {
        return correctGuessCount;
    }

    @JsonIgnore
    public int getGuessCountRatio() {
        return correctGuessCount - incorrectGuessCount;
    }

    public int getIncorrectGuessCount() {
        return incorrectGuessCount;
    }

    public int getShowAnswerCount() {
        return showAnswerCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return name.equals(player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
