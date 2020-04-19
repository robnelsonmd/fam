package fam.puzzle.controller;

import fam.puzzle.domain.Player;
import fam.puzzle.domain.Puzzle;
import fam.puzzle.service.PlayerService;
import fam.puzzle.service.PuzzleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
public class PuzzleController {
    private static final Logger LOG = LoggerFactory.getLogger(PuzzleController.class);

    private final PlayerService playerService;
    private final PuzzleService puzzleService;

    public PuzzleController(PlayerService playerService,
                            PuzzleService puzzleService) {
        this.playerService = playerService;
        this.puzzleService = puzzleService;
    }

    @PostMapping("/actions")
    public String actions(
            Model model,
            HttpSession httpSession,
            @RequestParam("showAnswer") Optional<String> showAnswer,
            @RequestParam("newPuzzle") Optional<String> newPuzzle
    ) {
        if (showAnswer.isPresent()) {
            LOG.info(String.format("%s showed the answer",getPlayer(httpSession)));
            incrementShowAnswerCount(httpSession);
            model.addAttribute("answer", getAnswer(httpSession));
            generateNewPuzzle(httpSession);
        }

        if (newPuzzle.isPresent()) {
            generateNewPuzzle(httpSession);
        }

        return "index";
    }

    @GetMapping("/cheat")
    public String cheat(Model model, HttpSession httpSession) {
        LOG.info(String.format("%s cheated!",getPlayer(httpSession)));
        incrementCheatCount(httpSession);
        model.addAttribute("cheat", String.format("CHEAT: The answer is %s",getAnswer(httpSession)));
        return "index";
    }

    @PostMapping("/guess")
    public String guess(
            Model model,
            HttpSession httpSession,
            @RequestParam("guess") String guess
    ) {
        try {
            int number = Integer.parseInt(guess);
            guess = String.format("%03d",number);

            if (getPuzzle(httpSession).isCorrectGuess(number)) {
                processCorrectGuess(model, httpSession);
            } else {
                processIncorrectGuess(model, httpSession, guess);
            }
        } catch (Exception e) {
            model.addAttribute("result",
                    String.format("(%s) is not a valid guess - try again",guess));
        }

        return "index";
    }

    @RequestMapping(value = {"","/","/index","/index.html"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(HttpSession httpSession) {
        generateNewPuzzle(httpSession);

        return "index";
    }

    @GetMapping("/players")
    public String players(HttpSession httpSession) {
        updatePlayers(httpSession, playerService.getPlayers());
        return "players";
    }

    private void generateNewPuzzle(HttpSession httpSession) {
        Player player = getPlayer(httpSession);
        LOG.info(String.format("Generating new puzzle for %s",player));
        updatePuzzle(httpSession, puzzleService.generateNewPuzzle(player));
    }

    private List<Integer> getAnswer(HttpSession httpSession) {
        return getPuzzle(httpSession).getAnswer();
    }

    private Player getPlayer(HttpSession httpSession) {
        return (Player) httpSession.getAttribute("player");
    }

    private Puzzle getPuzzle(HttpSession httpSession) {
        return (Puzzle) httpSession.getAttribute("puzzle");
    }

    private void incrementCheatCount(HttpSession httpSession) {
        Player player = getPlayer(httpSession);
        player = playerService.incrementCheatCount(player);
        updatePlayer(httpSession, player);
    }

    private void incrementCorrectGuessCount(HttpSession httpSession) {
        Player player = getPlayer(httpSession);
        player = playerService.incrementCorrectGuessCount(player);
        updatePlayer(httpSession, player);
    }

    private void incrementIncorrectGuessCount(HttpSession httpSession) {
        Player player = getPlayer(httpSession);
        player = playerService.incrementIncorrectGuessCount(player);
        updatePlayer(httpSession, player);
    }

    private void incrementShowAnswerCount(HttpSession httpSession) {
        Player player = getPlayer(httpSession);
        player = playerService.incrementShowAnswerCount(player);
        updatePlayer(httpSession, player);
    }

    private void processCorrectGuess(Model model, HttpSession httpSession) {
        LOG.info(String.format("%s guessed correctly",getPlayer(httpSession)));
        incrementCorrectGuessCount(httpSession);
        model.addAttribute("answer",
                String.format("The answer is: %s",getAnswer(httpSession)));
        model.addAttribute("result",
                "You guessed correctly!");
    }

    private void processIncorrectGuess(Model model, HttpSession httpSession, String guess) {
        LOG.info(String.format("%s guessed incorrectly",getPlayer(httpSession)));
        incrementIncorrectGuessCount(httpSession);
        model.addAttribute("result",
                String.format("%s is not the correct answer - try again",guess));
    }

    private void updatePlayer(HttpSession httpSession, Player player) {
        httpSession.setAttribute("player", player);
    }

    private void updatePlayers(HttpSession httpSession, List<Player> players) {
        httpSession.setAttribute("players", players);
    }

    private void updatePuzzle(HttpSession httpSession, Puzzle puzzle) {
        httpSession.setAttribute("puzzle", puzzle);
    }
}
