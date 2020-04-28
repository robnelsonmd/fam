package fam.puzzle.controller;

import fam.puzzle.TestContext;
import fam.puzzle.TestUtil;
import fam.puzzle.domain.Player;
import fam.puzzle.domain.Puzzle;
import fam.puzzle.service.PlayerService;
import fam.puzzle.service.PuzzleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = IndexController.class)
@ContextConfiguration(classes= TestContext.class)
class IndexControllerTest extends AbstractControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    PlayerService playerService;

    @MockBean
    PuzzleService puzzleService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void actions() {
    }

    @Test
    void cheat() {
    }

    @Test
    @WithUserDetails("joe")
    void testGettingNewPuzzle() throws Exception {
        Player player = TestUtil.getPlayer("Joe");
        Puzzle fourDigitPuzzle = TestUtil.getPuzzle(Arrays.asList(1,2,3,4));
        Puzzle threeDigitPuzzle = TestUtil.getPuzzle(Arrays.asList(1,2,3));

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("player", player);

        when(puzzleService.getNewPuzzle(3)).thenReturn(threeDigitPuzzle);
        when(puzzleService.getNewPuzzle(4)).thenReturn(fourDigitPuzzle);
        when(playerService.updatePuzzle(Mockito.any(Player.class), Mockito.any(Puzzle.class))).thenReturn(player);

        mockMvc.perform(MockMvcRequestBuilders.get("/generatePuzzle?size=3").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("index"));
        verify(puzzleService, times(1)).getNewPuzzle(3);
        assert threeDigitPuzzle.equals(session.getAttribute("puzzle"));

        mockMvc.perform(MockMvcRequestBuilders.get("/generatePuzzle?size=4").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("index"));
        verify(puzzleService, times(1)).getNewPuzzle(4);
        assert fourDigitPuzzle.equals(session.getAttribute("puzzle"));
    }

    @Test
    @WithUserDetails("joe")
    void testGettingPuzzleFromPlayer() throws Exception {
        Player player = TestUtil.getPlayer("Joe");
        Puzzle fourDigitPuzzle = TestUtil.getPuzzle(Arrays.asList(1,2,3,4));
        Puzzle threeDigitPuzzle = TestUtil.getPuzzle(Arrays.asList(1,2,3));
        player.setPuzzle(fourDigitPuzzle, 4);
        player.setPuzzle(threeDigitPuzzle, 3);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("player", player);

        mockMvc.perform(MockMvcRequestBuilders.get("/generatePuzzle?size=3").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("index"));
        assert threeDigitPuzzle.equals(session.getAttribute("puzzle"));

        mockMvc.perform(MockMvcRequestBuilders.get("/generatePuzzle?size=4").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("index"));
        assert fourDigitPuzzle.equals(session.getAttribute("puzzle"));
    }

    @Test
    void guess() {
    }

    @Test
    @WithUserDetails("joe")
    void home() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("puzzle", TestUtil.EMPTY_PUZZLE);
        mockMvc.perform(MockMvcRequestBuilders.get("/home").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("index"));
        assert session.getAttribute("puzzle") == null;
    }

    @Test
    void testIndexWithNoUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/index"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithUserDetails("joe")
    void testIndexWithValidUser() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("player", TestUtil.JOE);
        mockMvc.perform(MockMvcRequestBuilders.get("/index").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Welcome, Joe!")));
    }

    @Test
    @WithUserDetails("admin")
    void testIndexWithInvalidUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/index"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/accessDenied"));
    }

    @Test
    @WithUserDetails("admin")
    void testAdminLogout() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("index"));
    }

    @Test
    @WithUserDetails("joe")
    void testUserLogout() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("index"));
    }
}