package app.labs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SmartLogisticsController {
	
	@GetMapping("/admin/main")
    public String mainPage(Model model) {
        return "thymeleaf/html/admin_main";
    }
}
