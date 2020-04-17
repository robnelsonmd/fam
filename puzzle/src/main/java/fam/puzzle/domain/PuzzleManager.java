package fam.puzzle.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PuzzleManager {
    private Map<String,Puzzle> puzzleMap = new HashMap<>();

    public Puzzle getPuzzle(String name) {
        return puzzleMap.get(name);
    }
}
