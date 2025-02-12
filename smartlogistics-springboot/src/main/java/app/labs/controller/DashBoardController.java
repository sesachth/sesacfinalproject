package app.labs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashBoardController {
	
	@GetMapping("/admin/dashboard")
    public String mainPage(Model model) {
		model.addAttribute("page", "dashboard");
		
        return "thymeleaf/html/admin/admin_dashboard";
    }
}
