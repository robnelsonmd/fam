package fam.puzzle.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fam.core.util.StringUtil;
import fam.puzzle.security.PuzzleGrantedAuthority;
import fam.puzzle.security.PuzzleUser;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Player extends PuzzleUser implements Serializable {
    private static final long serialVersionUID = 3790114787071542585L;

    public static class Builder {
        private final String name;
        private final List<PuzzleGrantedAuthority> authorities = new ArrayList<>();
        private String emailAddress;
        private boolean receiveEmails;
        private int cheatCount;
        private int correctFourDigitGuessCount;
        private int correctThreeDigitGuessCount;
        private int incorrectFourDigitGuessCount;
        private int incorrectThreeDigitGuessCount;
        private int showAnswerCount;

        private Builder(Player player) {
            this.name = player.getName();
            this.authorities.addAll(player.getAuthorities().stream().map(a -> (PuzzleGrantedAuthority)a).collect(Collectors.toList()));
            this.emailAddress =  player.emailAddress;
            this.receiveEmails = player.receiveEmails;
            this.cheatCount =  player.cheatCount;
            this.correctFourDigitGuessCount =  player.correctFourDigitGuessCount;
            this.correctThreeDigitGuessCount =  player.correctThreeDigitGuessCount;
            this.incorrectFourDigitGuessCount =  player.incorrectFourDigitGuessCount;
            this.incorrectThreeDigitGuessCount =  player.incorrectThreeDigitGuessCount;
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

        public Builder incrementFourDigitCorrectGuessCount() {
            this.correctFourDigitGuessCount++;
            return this;
        }

        public Builder incrementFourDigitIncorrectGuessCount() {
            this.incorrectFourDigitGuessCount++;
            return this;
        }

        public Builder incrementThreeDigitCorrectGuessCount() {
            this.correctThreeDigitGuessCount++;
            return this;
        }

        public Builder incrementThreeDigitIncorrectGuessCount() {
            this.incorrectThreeDigitGuessCount++;
            return this;
        }

        public Builder incrementShowAnswerCount() {
            this.showAnswerCount++;
            return this;
        }

        public Player build() {
            return new Player(
                    name,
                    authorities,
                    emailAddress,
                    receiveEmails,
                    cheatCount,
                    correctThreeDigitGuessCount,
                    incorrectThreeDigitGuessCount,
                    correctFourDigitGuessCount,
                    incorrectFourDigitGuessCount,
                    showAnswerCount
            );
        }
    }

    private final String emailAddress;
    private final boolean receiveEmails;
    private final int cheatCount;
    private final int correctFourDigitGuessCount;
    private final int correctThreeDigitGuessCount;
    private final int incorrectFourDigitGuessCount;
    private final int incorrectThreeDigitGuessCount;
    private final int showAnswerCount;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Player(
            @JsonProperty("name") String name,
            @JsonProperty("authorities") Collection<PuzzleGrantedAuthority> authorities,
            @JsonProperty("emailAddress") String emailAddress,
            @JsonProperty("receiveEmails") boolean receiveEmails,
            @JsonProperty("cheatCount") int cheatCount,
            @JsonProperty("correctGuessCount")
            @JsonAlias({"correctGuessCount","correctThreeDigitGuessCount"})
                    int correctThreeDigitGuessCount,
            @JsonProperty("incorrectGuessCount")
            @JsonAlias({"incorrectGuessCount", "incorrectThreeDigitGuessCount"})
                    int incorrectThreeDigitGuessCount,
            @JsonProperty("correctFourDigitGuessCount") int correctFourDigitGuessCount,
            @JsonProperty("incorrectFourDigitGuessCount") int incorrectFourDigitGuessCount,
            @JsonProperty("showAnswerCount") int showAnswerCount
    ) {
        super(name, authorities);

        if (StringUtil.isEmptyString(name)) {
            throw new IllegalArgumentException(String.format("Invalid player name (%s)",name));
        }

        this.emailAddress = emailAddress;
        this.receiveEmails = receiveEmails;
        this.cheatCount = cheatCount;
        this.correctThreeDigitGuessCount = correctThreeDigitGuessCount;
        this.incorrectThreeDigitGuessCount = incorrectThreeDigitGuessCount;
        this.correctFourDigitGuessCount = correctFourDigitGuessCount;
        this.incorrectFourDigitGuessCount = incorrectFourDigitGuessCount;
        this.showAnswerCount = showAnswerCount;
    }

    public Player(String name) {
        this(name, Collections.singletonList(new PuzzleGrantedAuthority("USER")), "", false, 0, 0, 0, 0, 0, 0);
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

    public int getCorrectFourDigitGuessCount() {
        return correctFourDigitGuessCount;
    }

    public int getCorrectThreeDigitGuessCount() {
        return correctThreeDigitGuessCount;
    }

    @JsonIgnore
    public int getGuessCountRatio(int size) {
        switch (size) {
            case 3:
                return (correctThreeDigitGuessCount - incorrectThreeDigitGuessCount);

            case 4:
                return (correctFourDigitGuessCount - incorrectFourDigitGuessCount);

            default:
                throw new IllegalArgumentException("Invalid puzzle size: " + size);
        }
    }

    public int getIncorrectFourDigitGuessCount() {
        return incorrectFourDigitGuessCount;
    }

    public int getIncorrectThreeDigitGuessCount() {
        return incorrectThreeDigitGuessCount;
    }

    public int getShowAnswerCount() {
        return showAnswerCount;
    }

    @JsonIgnore
    public boolean hasPlayedGame(int size) {
        switch (size) {
            case 3:
                return ((correctThreeDigitGuessCount > 0) || (incorrectThreeDigitGuessCount > 0));

            case 4:
                return ((correctFourDigitGuessCount > 0) || (incorrectFourDigitGuessCount > 0));

            default:
                throw new IllegalArgumentException("Invalid puzzle size: " + size);
        }
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
