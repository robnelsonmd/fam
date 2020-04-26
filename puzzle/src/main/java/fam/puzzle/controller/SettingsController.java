package fam.puzzle.controller;

import fam.messaging.text.CellCarrier;
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
        model.addAttribute("cellCarriers", CellCarrier.values());
        return "settings";
    }

    @PostMapping("/settings")
    public String settingsPost(
            @ModelAttribute Player updatedPlayer,
            HttpSession session
    ) {
        Player player = getPlayer(session);

        if (areEmailSettingsDifferent(player, updatedPlayer)) {
            player = playerService.updateEmailSettings(player, updatedPlayer.isReceiveEmails(), updatedPlayer.getEmailAddress());
        }

        if (areTextSettingsDifferent(player, updatedPlayer)) {
            player = playerService.updateTextSettings(player, updatedPlayer.isReceiveTexts(), updatedPlayer.getCellCarrier(), updatedPlayer.getCellNumber());
        }

        updatePlayer(session, player);

        return "index";
    }

    private boolean areEmailSettingsDifferent(Player player1, Player player2) {
        return (player1.isReceiveEmails() != player2.isReceiveEmails()) ||
                !Objects.equals(player1.getEmailAddress(), player2.getEmailAddress());
    }

    private boolean areTextSettingsDifferent(Player player1, Player player2) {
        return (player1.isReceiveTexts() != player2.isReceiveTexts()) ||
                (player1.getCellCarrier() != player2.getCellCarrier()) ||
                !Objects.equals(player1.getCellNumber(), player2.getCellNumber());
    }
}
