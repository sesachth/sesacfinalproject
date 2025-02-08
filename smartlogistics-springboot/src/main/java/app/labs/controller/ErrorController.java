package app.labs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

	@GetMapping("/access-denied")
	public String accessDeniedPage(Model model) {
	    model.addAttribute("errorMessage", "작업자는 관리자 페이지에 접근할 수 없습니다.");
	    return "thymeleaf/html/access_denied";
	}

}
