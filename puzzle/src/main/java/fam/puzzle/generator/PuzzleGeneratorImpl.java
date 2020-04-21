package fam.puzzle.generator;

import fam.puzzle.domain.Puzzle;
import fam.puzzle.domain.ThreeDigitPuzzle;

public class PuzzleGeneratorImpl implements PuzzleGenerator {
    @Override
    public Puzzle generateThreeDigitPuzzle() {
        return ThreeDigitPuzzle.newInstance();
    }
}
