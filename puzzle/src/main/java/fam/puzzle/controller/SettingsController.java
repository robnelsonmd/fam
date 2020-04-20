package fam.puzzle.controller;

import fam.puzzle.domain.Player;
import fam.puzzle.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class SettingsController extends AbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(SettingsController.class);

    private final PlayerService playerService;

    public SettingsController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/settings")
    public String settingsGet(HttpSession session) {
        LOG.info(String.format("%s visited the settings page",getPlayer(session)));
        return "settings";
    }

    @PostMapping("/settings")
    public String settingsPost(
            HttpSession session,
            @RequestParam("emailAddress") String emailAddress,
            @RequestParam("receiveEmails") Optional<Boolean> receiveEmails
    ) {
        Player player = getPlayer(session);

        if (!player.getEmailAddress().equals(emailAddress)) {
            LOG.info(String.format("%s updated their email to %s",player.getName(),emailAddress));
            player = playerService.updateEmailAddress(player, emailAddress);
        }

        boolean newReceiveEmailsSetting = receiveEmails.orElse(false);
        if (player.isReceiveEmails() != newReceiveEmailsSetting) {
            LOG.info(String.format("%s updated their receive emails setting to %s",player.getName(),newReceiveEmailsSetting));
            player = playerService.updateReceiveEmails(player, newReceiveEmailsSetting);
        }

        updatePlayer(session, player);

        return "index";
    }
}
