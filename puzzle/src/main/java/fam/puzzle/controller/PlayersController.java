package fam.puzzle.controller;

import fam.puzzle.domain.Player;
import fam.puzzle.service.PlayerService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class PlayersController {
    private final PlayerService playerService;

    public PlayersController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/players")
    public String players(HttpSession httpSession) {
        updatePlayers(httpSession, playerService.getPlayers());
        return "players";
    }

    private void updatePlayers(HttpSession httpSession, List<Player> players) {
        httpSession.setAttribute("players", players);
    }
}
