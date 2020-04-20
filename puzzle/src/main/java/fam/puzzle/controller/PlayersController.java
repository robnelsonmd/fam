package fam.puzzle.controller;

import fam.puzzle.domain.Player;
import fam.puzzle.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class PlayersController extends AbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(PlayersController.class);

    private final PlayerService playerService;

    public PlayersController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/players")
    public String players(HttpSession session) {
        LOG.info(String.format("%s visited the players page",getPlayer(session)));
        updatePlayers(session, playerService.getPlayers());
        return "players";
    }

    private void updatePlayers(HttpSession session, List<Player> players) {
        session.setAttribute("players", players);
    }
}
