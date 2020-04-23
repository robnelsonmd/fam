package fam.puzzle.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fam.core.util.StringUtil;
import fam.puzzle.security.PuzzleGrantedAuthority;
import fam.puzzle.security.PuzzleUser;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class Player extends PuzzleUser implements Serializable {
    private static final long serialVersionUID = 3790114787071542585L;

    private String emailAddress;
    private boolean receiveEmails;
    private int cheatCount;
    private int correctFourDigitGuessCount;
    private int correctThreeDigitGuessCount;
    private int incorrectFourDigitGuessCount;
    private int incorrectThreeDigitGuessCount;
    private int showAnswerCount;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Player(
            @JsonProperty("name") String name,
            @JsonProperty("authorities") Collection<PuzzleGrantedAuthority> authorities)
    {
        super(name, authorities);

        if (StringUtil.isEmptyString(name)) {
            throw new IllegalArgumentException(String.format("Invalid player name (%s)", name));
        }
    }

    public Player(String name) {
        this(name, Collections.singletonList(new PuzzleGrantedAuthority("USER")));
    }

    public Player() {
        this("FORM-PLAYER");
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public boolean isReceiveEmails() {
        return receiveEmails;
    }

    public void setReceiveEmails(boolean receiveEmails) {
        this.receiveEmails = receiveEmails;
    }

    public int getCheatCount() {
        return cheatCount;
    }

    public void setCheatCount(int cheatCount) {
        this.cheatCount = cheatCount;
    }

    public int getCorrectFourDigitGuessCount() {
        return correctFourDigitGuessCount;
    }

    public void setCorrectFourDigitGuessCount(int correctFourDigitGuessCount) {
        this.correctFourDigitGuessCount = correctFourDigitGuessCount;
    }

    public int getCorrectThreeDigitGuessCount() {
        return correctThreeDigitGuessCount;
    }

    public void setCorrectThreeDigitGuessCount(int correctThreeDigitGuessCount) {
        this.correctThreeDigitGuessCount = correctThreeDigitGuessCount;
    }

    public int getIncorrectFourDigitGuessCount() {
        return incorrectFourDigitGuessCount;
    }

    public void setIncorrectFourDigitGuessCount(int incorrectFourDigitGuessCount) {
        this.incorrectFourDigitGuessCount = incorrectFourDigitGuessCount;
    }

    public int getIncorrectThreeDigitGuessCount() {
        return incorrectThreeDigitGuessCount;
    }

    public void setIncorrectThreeDigitGuessCount(int incorrectThreeDigitGuessCount) {
        this.incorrectThreeDigitGuessCount = incorrectThreeDigitGuessCount;
    }

    public int getShowAnswerCount() {
        return showAnswerCount;
    }

    public void setShowAnswerCount(int showAnswerCount) {
        this.showAnswerCount = showAnswerCount;
    }

    public String getName() {
        return getUsername();
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
