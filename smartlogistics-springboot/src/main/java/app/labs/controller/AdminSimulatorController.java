package app.labs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminSimulatorController {

	@GetMapping("/admin/simulator")
    public String adminSimulatorPage(Model model) {
        return "thymeleaf/html/admin_simulator";
    }
}
