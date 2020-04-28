package fam.puzzle.domain;

import fam.puzzle.generator.hint.AllNumbersIncorrectHint;
import fam.puzzle.generator.hint.Hint;
import fam.puzzle.generator.hint.HintType;
import fam.puzzle.generator.hint.NumberSequenceHint;
import fam.puzzle.util.PuzzleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Puzzle implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(Puzzle.class);
    private static final Pattern HINT_PATTERN = Pattern.compile("((\\d):([\\d]{5}))");
    private static final Pattern INCORRECT_GUESS_COUNT_PATTERN = Pattern.compile("^\\d+:(\\d+):.*$");
    private static final Pattern PUZZLE_ANSWER_PATTERN = Pattern.compile("^(\\d+):\\d+:.*$");

    private final List<Hint> hints;
    private final List<Integer> answer;
    private final int size;

    private Integer incorrectGuessCount = 0;

    public Puzzle(List<Integer> answer, List<Hint> hints) {
        this(answer, hints, 0);
    }

    public Puzzle(List<Integer> answer, List<Hint> hints, int incorrectGuessCount) {
        this.answer = answer;
        this.hints = hints;
        this.incorrectGuessCount = incorrectGuessCount;
        this.size = answer.size();
    }

    public static Puzzle deserialize(String puzzleString) {
        try {
            List<Integer> answer = parseAnswerFromPuzzleString(puzzleString);
            int guessCount = parseIncorrectGuessCountFromPuzzleString(puzzleString);
            List<Hint> hints = parseHintFromPuzzleString(puzzleString);
            return new Puzzle(answer, hints, guessCount);
        } catch (Exception e) {
            LOG.error(String.format("Unable to parse answer from puzzle string (%s) - aborting",puzzleString), e);
            return null;
        }
    }

    private static List<Integer> parseAnswerFromPuzzleString(String puzzleString) {
        Matcher matcher = PUZZLE_ANSWER_PATTERN.matcher(puzzleString);
        String matchString = matcher.matches() ? matcher.group(1) : null;

        if (matchString == null) {
            throw new IllegalArgumentException(String.format("Unable to parse answer from puzzle string (%s) - aborting",puzzleString));
        }

        return PuzzleUtil.deserializeNumberList(matchString);
    }

    private static List<Hint> parseHintFromPuzzleString(String puzzleString) {
        Matcher matcher = HINT_PATTERN.matcher(puzzleString);

        List<Hint> results = new ArrayList<>();
        while (matcher.find()) {
            HintType hintType = HintType.fromValue(Integer.parseInt(matcher.group(2)));
            String hintString = matcher.group(3);
            results.add(parseHintFromString(hintType, hintString));
        }

        return results;
    }

    private static Hint parseHintFromString(HintType hintType, String hintString) {
        switch(hintType) {
            case ALL_WRONG:
                return AllNumbersIncorrectHint.deserialize(hintString);
            case NUMBER_SEQUENCE:
                return NumberSequenceHint.deserialize(hintString);
            default:
                throw new IllegalArgumentException("Invalid hint type: " + hintType);
        }
    }

    public static int parseIncorrectGuessCountFromPuzzleString(String puzzleString) {
        Matcher matcher = INCORRECT_GUESS_COUNT_PATTERN.matcher(puzzleString);
        String matchString = matcher.matches() ? matcher.group(1) : null;

        if (matchString == null) {
            throw new IllegalArgumentException(String.format("Unable to parse incorrect guess count from puzzle string (%s) - aborting",puzzleString));
        }

        return Integer.parseInt(matchString);
    }

    public List<Integer> getAnswer() {
        return answer;
    }

    public List<Hint> getHints() {
        return hints;
    }

    public Integer getIncorrectGuessCount() {
        return incorrectGuessCount;
    }

    public int getSize() {
        return size;
    }

    public String getSizeString() {
        return PuzzleUtil.getPuzzleSizeString(size);
    }

    public boolean isCorrectGuess(int guess) {
        boolean correctGuess = answer.equals(PuzzleUtil.convertToNumberSequence(guess, size));
        if (!correctGuess) incorrectGuessCount++;
        return correctGuess;
    }

    public String serialize() {
        return String.format("%s:%d:%s",PuzzleUtil.serializeNumberList(answer),incorrectGuessCount,hints.stream().map(Hint::serialize).collect(Collectors.toList())).replaceAll(" ","");
    }
}
