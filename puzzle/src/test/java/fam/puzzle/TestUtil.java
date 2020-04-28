package fam.puzzle;

import fam.puzzle.domain.Player;
import fam.puzzle.domain.Puzzle;
import fam.puzzle.security.PuzzleGrantedAuthority;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestUtil {
    public static Player ADMIN = getAdmin("Admin");
    public static Player JOE = getPlayer("Joe");
    public static Puzzle EMPTY_PUZZLE = getPuzzle(Collections.emptyList());

    public static Player getAdmin(String name) {
        return new Player(name, Arrays.asList(PuzzleGrantedAuthority.ADMIN));
    }

    public static Player getPlayer(String name) {
        return new Player(name);
    }

    public static Puzzle getPuzzle(List<Integer> answer) {
        return new Puzzle(answer, Collections.emptyList());
    }
}
