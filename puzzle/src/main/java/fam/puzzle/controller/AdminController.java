package fam.puzzle.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AdminController extends AbstractController {
    public AdminController(String applicationVersion) {
        super(applicationVersion);
    }

    @RequestMapping(value = "/accessDenied", method = {RequestMethod.GET, RequestMethod.POST})
    public String accessDenied() {
        return "accessDenied";
    }

    @GetMapping("/admin")
    public String adminGet() {
        return "admin";
    }
}
