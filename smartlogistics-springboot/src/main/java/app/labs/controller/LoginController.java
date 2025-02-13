package app.labs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

	@GetMapping("/login")
    public String loginPage() {
        return "thymeleaf/html/common/login";  // 로그인 페이지로 이동
	}
}
