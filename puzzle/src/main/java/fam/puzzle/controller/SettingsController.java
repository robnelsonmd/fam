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

        if (player.isReceiveEmails() != updatedPlayer.isReceiveEmails()) {
            LOG.info(String.format("%s updated their receive emails setting to %s",player,updatedPlayer.isReceiveEmails()));
            player.setReceiveEmails(updatedPlayer.isReceiveEmails());
            player = playerService.savePlayer(player);
        }

        if (!Objects.equals(player.getEmailAddress(), updatedPlayer.getEmailAddress())) {
            LOG.info(String.format("%s updated their email to %s",player,updatedPlayer.getEmailAddress()));
            player.setEmailAddress(updatedPlayer.getEmailAddress());
            player = playerService.savePlayer(player);
        }

        if (player.isReceiveTexts() != updatedPlayer.isReceiveTexts()) {
            LOG.info(String.format("%s updated their receive texts setting to %s",player,updatedPlayer.isReceiveTexts()));
            player.setReceiveTexts(updatedPlayer.isReceiveTexts());
            player = playerService.savePlayer(player);
        }

        if ((player.getCellCarrier() != updatedPlayer.getCellCarrier()) ||
                !Objects.equals(player.getCellNumber(), updatedPlayer.getCellNumber())) {
            LOG.info(String.format("%s updated their cell info to %s:%s",player,updatedPlayer.getCellCarrier(),updatedPlayer.getCellNumber()));
            playerService.updateCellInfo(player, updatedPlayer.getCellCarrier(), updatedPlayer.getCellNumber());
        }

        updatePlayer(session, player);

        return "index";
    }
}
