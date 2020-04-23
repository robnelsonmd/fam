package fam.puzzle.controller;

import fam.puzzle.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class RankingsController extends AbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(RankingsController.class);

    private final PlayerService playerService;

    public RankingsController(String applicationVersion,
                              PlayerService playerService) {
        super(applicationVersion);
        this.playerService = playerService;
    }

    @GetMapping("/rankings")
    public String rankings(Model model, HttpSession session) {
        LOG.info(String.format("%s visited the rankings page",getPlayer(session)));
        model.addAttribute("threeDigitPuzzleRankings",playerService.getPlayerRankings(3));
        model.addAttribute("fourDigitPuzzleRankings",playerService.getPlayerRankings(4));
        return "rankings";
    }
}
