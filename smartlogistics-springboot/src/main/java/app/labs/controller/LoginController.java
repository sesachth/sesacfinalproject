package app.labs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

	@GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        return "thymeleaf/html/login";
    }
	
	/*
	private final UserDetailsService userDetailsService;

    public LoginController(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    
    @GetMapping("/login")
    public String loginPage() {
        return "thymeleaf/html/login";
    }

    @GetMapping("/admin-login")
    public RedirectView adminLogin(HttpServletRequest request) {
        return autoLogin("admin", "admin", "/admin/main", request);
    }

    @GetMapping("/worker-login")
    public RedirectView workerLogin(HttpServletRequest request) {
        return autoLogin("worker", "worker", "/worker/simulator", request);
    }

    private RedirectView autoLogin(String username, String password, String redirectUrl, HttpServletRequest request) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);  // 변경된 부분!

        Authentication authentication = 
                new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 세션에 SecurityContext를 적용
        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        return new RedirectView(redirectUrl);
    }
    */
}
