package fam.puzzle.controller;

import fam.puzzle.domain.Player;
import fam.puzzle.domain.Puzzle;
import fam.puzzle.service.PlayerService;
import fam.puzzle.service.PuzzleService;
import fam.puzzle.util.PuzzleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
public class IndexController extends AbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(IndexController.class);

    private final PlayerService playerService;
    private final PuzzleService puzzleService;

    public IndexController(String applicationVersion,
                           PlayerService playerService,
                           PuzzleService puzzleService) {
        super(applicationVersion);
        this.playerService = playerService;
        this.puzzleService = puzzleService;
    }

    @PostMapping("/actions")
    public String actions(
            Model model,
            HttpSession session,
            @RequestParam("showAnswer") Optional<String> showAnswer,
            @RequestParam("newPuzzle") Optional<String> newPuzzle
    ) {
        if (showAnswer.isPresent()) {
            LOG.info(String.format("%s showed the answer",getPlayer(session)));
            incrementShowAnswerCount(session);
            model.addAttribute("answer", getAnswer(session));
        }

        if (newPuzzle.isPresent()) {
            generateNewPuzzle(session);
        }

        return "index";
    }

    @GetMapping("/cheat")
    public String cheat(Model model, HttpSession session) {
        LOG.info(String.format("%s cheated!",getPlayer(session)));
        incrementCheatCount(session);
        model.addAttribute("cheat", String.format("CHEAT: The answer is %s",getAnswer(session)));
        return "index";
    }

    @GetMapping("/generatePuzzle")
    public String generatePuzzle(
            HttpSession session,
            @RequestParam("size") int size
    ) {
        LOG.info(String.format("Generating new puzzle for %s",getPlayer(session)));
        updatePuzzle(session, puzzleService.generateNewPuzzle(size));

        return "redirect:index";
    }

    @PostMapping("/guess")
    public String guess(
            Model model,
            HttpSession session,
            @RequestParam("guess") String guess
    ) {
        try {
            int number = Integer.parseInt(guess.replaceAll("[\\[, \\]]*", ""));

            if (getPuzzle(session).isCorrectGuess(number)) {
                processCorrectGuess(model, session);
            } else {
                guess = PuzzleUtil.getFormattedNumberString(number, getPuzzle(session).getSize());
                processIncorrectGuess(model, session, guess);
            }
        } catch (Exception e) {
            model.addAttribute("result",
                    String.format("(%s) is not a valid guess - try again",guess));
        }

        return "index";
    }

    @GetMapping("/home")
    public String home(HttpSession session) {
        updatePuzzle(session, null);
        return "redirect:index";
    }

    @RequestMapping(value = {"","/","/index","/index.html"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index() {
        return "index";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return "redirect:index";
    }

    private void generateNewPuzzle(HttpSession session) {
        LOG.info(String.format("Generating new puzzle for %s",getPlayer(session)));
        Puzzle puzzle = getPuzzle(session);
        int puzzleSize = (puzzle != null) ? puzzle.getSize() : 3;
        updatePuzzle(session, puzzleService.generateNewPuzzle(puzzleSize));
    }

    private List<Integer> getAnswer(HttpSession session) {
        return getPuzzle(session).getAnswer();
    }

    private Puzzle getPuzzle(HttpSession session) {
        return (Puzzle) session.getAttribute("puzzle");
    }

    private void incrementCheatCount(HttpSession session) {
        Player player = getPlayer(session);
        player = playerService.incrementCheatCount(player);
        updatePlayer(session, player);
    }

    private void incrementCorrectGuessCount(HttpSession session) {
        Player player = getPlayer(session);
        Puzzle puzzle = getPuzzle(session);
        player = playerService.incrementCorrectGuessCount(player, puzzle.getSize());
        updatePlayer(session, player);
    }

    private void incrementIncorrectGuessCount(HttpSession session) {
        Player player = getPlayer(session);
        Puzzle puzzle = getPuzzle(session);
        player = playerService.incrementIncorrectGuessCount(player, puzzle.getSize());
        updatePlayer(session, player);
    }

    private void incrementShowAnswerCount(HttpSession session) {
        Player player = getPlayer(session);
        player = playerService.incrementShowAnswerCount(player);
        updatePlayer(session, player);
    }

    private void processCorrectGuess(Model model, HttpSession session) {
        LOG.info(String.format("%s guessed correctly",getPlayer(session)));
        incrementCorrectGuessCount(session);
        model.addAttribute("answer",
                String.format("The answer is: %s",getAnswer(session)));
        model.addAttribute("result",
                "You guessed correctly!");
    }

    private void processIncorrectGuess(Model model, HttpSession session, String guess) {
        LOG.info(String.format("%s guessed incorrectly",getPlayer(session)));
        incrementIncorrectGuessCount(session);
        model.addAttribute("result",
                String.format("%s is not the correct answer - try again",guess));
    }

    private void updatePuzzle(HttpSession session, Puzzle puzzle) {
        session.setAttribute("puzzle", puzzle);
    }
}
