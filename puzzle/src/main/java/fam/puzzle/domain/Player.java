package fam.puzzle.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fam.core.util.StringUtil;
import fam.messaging.text.CellCarrier;
import fam.puzzle.security.PuzzleGrantedAuthority;
import fam.puzzle.security.PuzzleUser;

import java.io.Serializable;
import java.util.*;

@JsonInclude(JsonInclude.Include. NON_NULL)
public class Player extends PuzzleUser implements Serializable {
    private static final long serialVersionUID = 3790114787071542585L;

    private final Map<Integer,Integer> correctGuessCounts = new HashMap<>();
    private final Map<Integer,Integer> incorrectGuessCounts = new HashMap<>();
    private CellCarrier cellCarrier = CellCarrier.ATT;
    private String cellNumber;
    private String emailAddress;
    private String textAddress;
    private boolean receiveEmails;
    private boolean receiveTexts;
    private int cheatCount;
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

    public CellCarrier getCellCarrier() {
        return cellCarrier;
    }

    public void setCellCarrier(CellCarrier cellCarrier) {
        this.cellCarrier = cellCarrier;
    }

    public String getCellNumber() {
        return cellNumber;
    }

    public void setCellNumber(String cellNumber) {
        this.cellNumber = cellNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getTextAddress() {
        return textAddress;
    }

    public void setTextAddress(String textAddress) {
        this.textAddress = textAddress;
    }

    public boolean isReceiveEmails() {
        return receiveEmails;
    }

    public void setReceiveEmails(boolean receiveEmails) {
        this.receiveEmails = receiveEmails;
    }

    public boolean isReceiveTexts() {
        return receiveTexts;
    }

    public void setReceiveTexts(boolean receiveTexts) {
        this.receiveTexts = receiveTexts;
    }

    public int getCheatCount() {
        return cheatCount;
    }

    public void setCheatCount(int cheatCount) {
        this.cheatCount = cheatCount;
    }

    public int getCorrectFourDigitGuessCount() {
        return correctGuessCounts.getOrDefault(4, 0);
    }

    public void setCorrectFourDigitGuessCount(int count) {
        correctGuessCounts.put(4, count);
    }

    @JsonIgnore
    public int getCorrectGuessCount(int puzzleSize) {
        return correctGuessCounts.getOrDefault(puzzleSize, 0);
    }

    public void setCorrectGuessCount(int puzzleSize, int guessCount) {
        correctGuessCounts.put(puzzleSize, guessCount);
    }

    public int getCorrectThreeDigitGuessCount() {
        return correctGuessCounts.getOrDefault(3, 0);
    }

    public void setCorrectThreeDigitGuessCount(int count) {
        correctGuessCounts.put(3, count);
    }

    public int getIncorrectFourDigitGuessCount() {
        return incorrectGuessCounts.getOrDefault(4, 0);
    }

    public void setIncorrectFourDigitGuessCount(int count) {
        incorrectGuessCounts.put(4, count);
    }

    @JsonIgnore
    public int getIncorrectGuessCount(int puzzleSize) {
        return incorrectGuessCounts.getOrDefault(puzzleSize, 0);
    }

    public void setIncorrectGuessCount(int puzzleSize, int guessCount) {
        correctGuessCounts.put(puzzleSize, guessCount);
    }

    public int getIncorrectThreeDigitGuessCount() {
        return incorrectGuessCounts.getOrDefault(3, 0);
    }

    public void setIncorrectThreeDigitGuessCount(int count) {
        incorrectGuessCounts.put(3, count);
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
        return getCorrectGuessCount(size) - getIncorrectGuessCount(size);
    }

    @JsonIgnore
    public boolean hasPlayedGame(int size) {
        return (correctGuessCounts.getOrDefault(size,0) > 0) ||
                (incorrectGuessCounts.getOrDefault(size,0) > 0);
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
