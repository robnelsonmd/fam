package fam.puzzle.controller;

import fam.puzzle.domain.Player;
import fam.puzzle.service.PlayerService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class SettingsController extends AbstractController {
    private final PlayerService playerService;

    public SettingsController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/settings")
    public String settingsGet() {
        return "settings";
    }

    @PostMapping("/settings")
    public String settingsPost(
            HttpSession session,
            @RequestParam("emailAddress") String emailAddress,
            @RequestParam("receiveEmails") Optional<Boolean> receiveEmails
    ) {
        Player player = getPlayer(session);
        player = playerService.updateEmailAddress(player, emailAddress);
        player = playerService.updateReceiveEmails(player, receiveEmails.isPresent());
        updatePlayer(session, player);

        return "index";
    }
}
