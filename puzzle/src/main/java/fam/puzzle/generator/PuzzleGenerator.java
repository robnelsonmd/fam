package fam.puzzle.generator;

import fam.puzzle.domain.Puzzle;

public interface PuzzleGenerator {
    Puzzle getPuzzle();
    int getPuzzleSize();
}
