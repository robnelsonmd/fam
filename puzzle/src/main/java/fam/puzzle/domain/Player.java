package fam.puzzle.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fam.core.util.StringUtil;
import fam.puzzle.security.PuzzleUser;

import java.io.Serializable;
import java.util.Objects;

public class Player extends PuzzleUser implements Serializable {
    private static final long serialVersionUID = 3790114787071542585L;

    public static class Builder {
        private final String name;
        private String emailAddress;
        private boolean receiveEmails;
        private int cheatCount;
        private int correctGuessCount;
        private int incorrectGuessCount;
        private int showAnswerCount;

        private Builder(Player player) {
            this.name = player.getName();
            this.emailAddress =  player.emailAddress;
            this.cheatCount =  player.cheatCount;
            this.correctGuessCount =  player.correctGuessCount;
            this.incorrectGuessCount =  player.incorrectGuessCount;
            this.showAnswerCount =  player.showAnswerCount;
        }

        public Builder updateEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
            return this;
        }

        public Builder updateReceiveEmails(boolean receiveEmails) {
            this.receiveEmails = receiveEmails;
            return this;
        }

        public Builder incrementCheatCount() {
            this.cheatCount++;
            return this;
        }

        public Builder incrementCorrectGuessCount() {
            this.correctGuessCount++;
            return this;
        }

        public Builder incrementIncorrectGuessCount() {
            this.incorrectGuessCount++;
            return this;
        }

        public Builder incrementShowAnswerCount() {
            this.showAnswerCount++;
            return this;
        }

        public Player build() {
            return new Player(
                    name,
                    emailAddress,
                    receiveEmails,
                    cheatCount,
                    correctGuessCount,
                    incorrectGuessCount,
                    showAnswerCount
            );
        }
    }

    private final String emailAddress;
    private final boolean receiveEmails;
    private final int cheatCount;
    private final int correctGuessCount;
    private final int incorrectGuessCount;
    private final int showAnswerCount;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Player(
            @JsonProperty("name") String name,
            @JsonProperty("emailAddress") String emailAddress,
            @JsonProperty("receiveEmails") boolean receiveEmails,
            @JsonProperty("cheatCount") int cheatCount,
            @JsonProperty("correctGuessCount") int correctGuessCount,
            @JsonProperty("incorrectGuessCount") int incorrectGuessCount,
            @JsonProperty("showAnswerCount") int showAnswerCount
    ) {
        super(name);

        if (StringUtil.isEmptyString(name)) {
            throw new IllegalArgumentException(String.format("Invalid player name (%s)",name));
        }

        this.emailAddress = emailAddress;
        this.receiveEmails = receiveEmails;
        this.cheatCount = cheatCount;
        this.correctGuessCount = correctGuessCount;
        this.incorrectGuessCount = incorrectGuessCount;
        this.showAnswerCount = showAnswerCount;
    }

    public Player(String name) {
        this(name, "", false, 0, 0, 0, 0);
    }

    public Builder playerBuilder() {
        return new Builder(this);
    }

    public String getName() {
        return getUsername();
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public boolean isReceiveEmails() {
        return receiveEmails;
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
        return getName().equals(player.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return getName();
    }
}
