package fam.puzzle.service;

import fam.puzzle.domain.Player;
import fam.puzzle.domain.Puzzle;

public interface PuzzleService {
    Puzzle generateNewPuzzle(Player player);
}
