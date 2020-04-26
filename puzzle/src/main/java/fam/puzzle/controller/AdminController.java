package fam.puzzle.controller;

import fam.messaging.text.CellCarrier;
import fam.puzzle.domain.Player;
import fam.puzzle.service.PlayerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class AdminController extends AbstractController {
    private final PlayerService playerService;

    public AdminController(String applicationVersion, PlayerService playerService) {
        super(applicationVersion);
        this.playerService = playerService;
    }

    @RequestMapping(value = "/accessDenied", method = {RequestMethod.GET, RequestMethod.POST})
    public String accessDenied() {
        return "accessDenied";
    }

    @GetMapping("/admin")
    public String adminGet() {
        return "admin";
    }

    @GetMapping("/admin/updatePlayer")
    public String updatePlayerGet(
            Model model,
            @RequestParam("name") String name
        ) {
        model.addAttribute("player", playerService.getPlayer(name));
        model.addAttribute("cellCarriers", CellCarrier.values());

        return "updatePlayer";
    }

    @PostMapping("/admin/updatePlayer")
    public String updatePlayerPost(
            Model model,
            HttpSession session,
            @RequestParam("name") String name,
            @RequestParam("receiveEmails") Optional<Boolean> receiveEmails,
            @RequestParam("emailAddress") String emailAddress,
            @RequestParam("receiveTexts") Optional<Boolean> receiveTexts,
            @RequestParam("cellCarrier") CellCarrier cellCarrier,
            @RequestParam("cellNumber") String cellNumber
        ) {
        Player player = playerService.getPlayer(name);
        playerService.updateEmailSettings(player, receiveEmails.orElse(false), emailAddress);
        playerService.updateTextSettings(player, receiveTexts.orElse(false), cellCarrier, cellNumber);

        return "redirect:/admin/userManagement";
    }

    @GetMapping("/admin/userManagement")
    public String userManagementGet(Model model) {
        model.addAttribute("players", playerService.getPlayers());

        return "userManagement";
    }
}
