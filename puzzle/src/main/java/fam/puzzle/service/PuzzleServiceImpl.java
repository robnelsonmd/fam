package fam.puzzle.service;

import fam.puzzle.domain.Puzzle;
import fam.puzzle.generator.PuzzleGenerator;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PuzzleServiceImpl implements PuzzleService {
    private final Map<Integer,PuzzleGenerator> puzzleGenerators = new HashMap<>();

    public PuzzleServiceImpl(List<PuzzleGenerator> puzzleGenerators) {
        puzzleGenerators.forEach(puzzleGenerator -> this.puzzleGenerators.put(puzzleGenerator.getPuzzleSize(), puzzleGenerator));
    }

    @Override
    public Puzzle getNewPuzzle(int size) {
        return puzzleGenerators.get(size).getPuzzle();
    }
}
