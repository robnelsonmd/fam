package fam.puzzle.controller;

import fam.puzzle.domain.Puzzle;
import fam.puzzle.domain.PuzzleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
public class IndexController {
    private static final Logger LOG = LoggerFactory.getLogger(IndexController.class);

    private final PuzzleManager puzzleManager;

    public IndexController(PuzzleManager puzzleManager) {
        this.puzzleManager = puzzleManager;
    }

    @RequestMapping(value = {"","/","/index","/index.html"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model, HttpSession httpSession) {
        if (getUsername(httpSession) == null) {
            return "login";
        }

        LOG.info(String.format("Generating new puzzle for %s",getUsername(httpSession)));
        model.addAttribute("puzzle", generateNewPuzzle(httpSession));
        return "index";
    }

    @GetMapping("/cheat")
    public String cheat(Model model, HttpSession httpSession) {
        LOG.info(String.format("%s cheated!",getUsername(httpSession)));
        model.addAttribute("cheat", String.format("CHEAT: The answer is %s",getAnswer(httpSession)));
        model.addAttribute("puzzle", getPuzzle(httpSession));
        return "index";
    }

    @PostMapping("/guess")
    public String guess(
            Model model,
            HttpSession httpSession,
            @RequestParam("guess") String guess,
            @RequestParam("submit") Optional<String> submit,
            @RequestParam("show") Optional<String> show
    ) {
        if (show.isPresent()) {
            return processShowRequest(model, httpSession);
        }

        if (submit.isPresent()) {
            return processGuessRequest(model, httpSession, guess);
        }

        throw new IllegalArgumentException("Invalid guess request");
    }

    @PostMapping("/login")
    public String login(
            Model model,
            HttpSession httpSession,
            @RequestParam("name") String name
    ) {
        httpSession.setAttribute("name", name);
        return index(model, httpSession);
    }

    private Puzzle generateNewPuzzle(HttpSession httpSession) {
        return puzzleManager.generateNewPuzzle(getUsername(httpSession));
    }

    private List<Integer> getAnswer(HttpSession httpSession) {
        return puzzleManager.getPuzzle(getUsername(httpSession)).getAnswer();
    }

    private Integer getIncorrectGuessCount(HttpSession httpSession) {
        return getPuzzle(httpSession).getIncorrectGuessCount();
    }

    private Puzzle getPuzzle(HttpSession httpSession) {
        return puzzleManager.getPuzzle(getUsername(httpSession));
    }

    private String getUsername(HttpSession httpSession) {
        return (String) httpSession.getAttribute("name");
    }

    private String processGuessRequest(Model model, HttpSession httpSession, String guess) {
        model.addAttribute("puzzle", getPuzzle(httpSession));

        try {
            int number = Integer.parseInt(guess);
            guess = String.format("%03d",number);

            if (getPuzzle(httpSession).isCorrectGuess(number)) {
                LOG.info(String.format("%s guessed correctly",getUsername(httpSession)));
                model.addAttribute("answer",
                        String.format("The answer is: %s",getAnswer(httpSession)));
                model.addAttribute("result",
                        "You guessed correctly!");
            } else {
                LOG.info(String.format("%s guessed incorrectly",getUsername(httpSession)));
                model.addAttribute("result",
                        String.format("%s is not the correct answer - try again",guess));
                model.addAttribute("incorrectGuessCount",
                        String.format("Number of incorrect guesses = %s",getIncorrectGuessCount(httpSession)));
            }
        } catch (Exception e) {
            model.addAttribute("result",
                    String.format("(%s) is not a valid guess - try again",guess));
        }

        return "index";
    }

    private String processShowRequest(Model model, HttpSession httpSession) {
        LOG.info(String.format("%s showed the answer",getUsername(httpSession)));
        model.addAttribute("puzzle", getPuzzle(httpSession));
        model.addAttribute("answer", String.format("The answer is: %s",getAnswer(httpSession)));
        generateNewPuzzle(httpSession);
        return "index";
    }
}
