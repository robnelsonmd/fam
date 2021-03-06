package fam.puzzle.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fam.messaging.text.CellCarrier;
import fam.puzzle.security.PuzzleGrantedAuthority;
import fam.puzzle.security.PuzzleUser;
import fambam.core.util.StringUtils;

import java.io.Serializable;
import java.util.*;

@JsonInclude(JsonInclude.Include. NON_NULL)
public class Player extends PuzzleUser implements Serializable {
    private static final long serialVersionUID = 3790114787071542585L;

    private final Map<Integer,Integer> correctGuessCounts = new HashMap<>();
    private final Map<Integer,Integer> incorrectGuessCounts = new HashMap<>();
    private final Map<Integer,Puzzle> puzzles = new HashMap<>();
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

        if (StringUtils.isEmptyString(name)) {
            throw new IllegalArgumentException(String.format("Invalid player name (%s)", name));
        }
    }

    public Player(String name) {
        this(name, Collections.singletonList(new PuzzleGrantedAuthority("USER")));
    }

    public Player() {
        this("FORM-PLAYER");
    }

    @JsonIgnore
    public Puzzle getPuzzle(int puzzleSize) {
        return this.puzzles.get(puzzleSize);
    }

    public void setPuzzle(Puzzle puzzle, int size) {
        this.puzzles.put(size, puzzle);
    }

    public String getFourDigitPuzzle() {
        Puzzle puzzle = puzzles.get(4);
        return (puzzle != null) ? puzzle.serialize() : null;
    }

    public void setFourDigitPuzzle(String puzzleString) {
        this.puzzles.put(4, Puzzle.deserialize(puzzleString));
    }

    public String getThreeDigitPuzzle() {
        Puzzle puzzle = puzzles.get(3);
        return (puzzle != null) ? puzzle.serialize() : null;
    }

    public void setThreeDigitPuzzle(String puzzleString) {
        this.puzzles.put(3, Puzzle.deserialize(puzzleString));
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

    @JsonIgnore
    public boolean isAdmin() {
        return getAuthorities().contains(PuzzleGrantedAuthority.ADMIN);
    }

    @JsonIgnore
    public boolean isUser() {
        return getAuthorities().contains(PuzzleGrantedAuthority.USER);
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

    @JsonIgnore
    public String getDetails() {
        return String.format("Player {%s, emails: %s/%s, texts: %s/%s/%s/%s, guesses: 3:%s/%s 4:%s/%s}",
                getName(), isReceiveEmails(), getEmailAddress(),
                isReceiveTexts(), getCellCarrier(), getCellNumber(), getTextAddress(),
                getCorrectGuessCount(3), getIncorrectGuessCount(3),
                getCorrectGuessCount(4), getIncorrectGuessCount(4));
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
        incorrectGuessCounts.put(puzzleSize, guessCount);
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
