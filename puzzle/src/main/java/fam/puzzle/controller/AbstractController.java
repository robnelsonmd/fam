package fam.puzzle.controller;

import fam.puzzle.domain.Player;

import javax.servlet.http.HttpSession;

public abstract class AbstractController {
    protected Player getPlayer(HttpSession session) {
        return (Player) session.getAttribute("player");
    }

    protected void updatePlayer(HttpSession session, Player player) {
        session.setAttribute("player", player);
    }
}
