package fam.puzzle.controller;

import fam.puzzle.domain.Player;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpSession;

public abstract class AbstractController {
    private final String applicationVersion;

    public AbstractController(String applicationVersion) {
        this.applicationVersion = applicationVersion;
    }

    protected Player getPlayer(HttpSession session) {
        return (Player) session.getAttribute("player");
    }

    protected void updatePlayer(HttpSession session, Player player) {
        session.setAttribute("player", player);
    }

    @ModelAttribute("applicationVersion")
    public String getApplicationVersion() {
        return applicationVersion;
    }
}
