package fam.puzzle.controller;

import fam.puzzle.domain.Player;
import fam.puzzle.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import java.util.Objects;

@Controller
public class SettingsController extends AbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(SettingsController.class);

    private final PlayerService playerService;

    public SettingsController(String applicationVersion,
                              PlayerService playerService) {
        super(applicationVersion);
        this.playerService = playerService;
    }

    @GetMapping("/settings")
    public String settingsGet(Model model, HttpSession session) {
        Player player = getPlayer(session);
        LOG.info(String.format("%s visited the settings page",player));
        model.addAttribute("player", player);
        return "settings";
    }

    @PostMapping("/settings")
    public String settingsPost(
            @ModelAttribute Player updatedPlayer,
            HttpSession session
    ) {
        Player player = getPlayer(session);

        if (!Objects.equals(player.getEmailAddress(), updatedPlayer.getEmailAddress())) {
            LOG.info(String.format("%s updated their email to %s",player,updatedPlayer.getEmailAddress()));
            player = playerService.updateEmailAddress(player, updatedPlayer.getEmailAddress());
        }

        if (player.isReceiveEmails() != updatedPlayer.isReceiveEmails()) {
            LOG.info(String.format("%s updated their receive emails setting to %s",player,updatedPlayer.isReceiveEmails()));
            player = playerService.updateReceiveEmails(player, updatedPlayer.isReceiveEmails());
        }

        updatePlayer(session, player);

        return "index";
    }
}
