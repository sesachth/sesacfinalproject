package app.labs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WorkerController {

	@GetMapping("/worker/packaging")
    public String mainPage(Model model) {
        return "thymeleaf/html/worker/worker_smartpackaging";
    }
}
