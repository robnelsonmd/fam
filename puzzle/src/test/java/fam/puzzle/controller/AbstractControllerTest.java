package fam.puzzle.controller;

import fam.puzzle.security.PuzzleUserDetailsService;
import org.springframework.boot.test.mock.mockito.MockBean;

public class AbstractControllerTest {
    @MockBean
    PuzzleUserDetailsService puzzleUserDetailsService;
}
