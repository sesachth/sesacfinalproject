package app.labs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StackingController {

	@GetMapping("/admin/stacking")
    public String adminSimulatorPage(Model model) {
		model.addAttribute("page", "stacking");
		
        return "thymeleaf/html/admin/admin_stacking";
    }
	
	@GetMapping("/worker/stacking")
    public String workerSimulatorPage(Model model) {
		model.addAttribute("page", "stacking");
		
        return "thymeleaf/html/worker/worker_stacking";
    }
}
