package fam.puzzle.service;

import fam.puzzle.domain.Puzzle;

public interface PuzzleService {
    Puzzle getNewPuzzle(int size);
}
