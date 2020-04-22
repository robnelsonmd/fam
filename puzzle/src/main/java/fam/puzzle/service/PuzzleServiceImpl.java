package fam.puzzle.service;

import fam.puzzle.domain.Player;
import fam.puzzle.domain.Puzzle;
import fam.puzzle.generator.PuzzleGenerator;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PuzzleServiceImpl implements PuzzleService {
    private final Map<Player,Puzzle> puzzleMap = new HashMap<>();
    private final PuzzleGenerator puzzleGenerator;

    public PuzzleServiceImpl(PuzzleGenerator puzzleGenerator) {
        this.puzzleGenerator = puzzleGenerator;
    }

    @Override
    public Puzzle generateNewPuzzle(Player player) {
        Puzzle puzzle = puzzleGenerator.generatePuzzle(3);
        puzzleMap.put(player, puzzle);
        return puzzle;
    }
}
