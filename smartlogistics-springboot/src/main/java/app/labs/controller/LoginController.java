/*
package app.labs.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class LoginController {
	
	private final UserDetailsManager userDetailsManager;
	
	@GetMapping("/login")
    public String mainPage() {
        return "thymeleaf/html/login";
    }

    public LoginController(UserDetailsManager userDetailsManager) {
        this.userDetailsManager = userDetailsManager;
    }

    @GetMapping("/admin-login")
    public RedirectView adminLogin() {
        return autoLogin("admin", "admin", "/admin/main");
    }

    @GetMapping("/worker-login")
    public RedirectView workerLogin() {
        return autoLogin("worker", "worker", "/worker/simulator");
    }

    private RedirectView autoLogin(String username, String password, String redirectUrl) {
        UserDetails userDetails = userDetailsManager.loadUserByUsername(username);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        return new RedirectView(redirectUrl);
    }
}
*/

package app.labs.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    private final UserDetailsManager userDetailsManager;
    private final AuthenticationManager authenticationManager;

    public LoginController(UserDetailsManager userDetailsManager, AuthenticationManager authenticationManager) {
        this.userDetailsManager = userDetailsManager;
        this.authenticationManager = authenticationManager;
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
        UserDetails userDetails = userDetailsManager.loadUserByUsername(username);
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(userDetails.getUsername(), password, userDetails.getAuthorities())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 세션에 SecurityContext를 적용
        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        return new RedirectView(redirectUrl);
    }
}
