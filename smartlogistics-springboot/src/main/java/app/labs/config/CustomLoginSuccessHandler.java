package app.labs.config;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	
	@Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        String targetUrl = determineTargetUrl(authentication);
        // getRedirectStrategy().sendRedirect(request, response, targetUrl);
        // JSON 응답 반환
        response.setContentType("text/plain; charset=UTF-8");
        response.getWriter().write(targetUrl);
    }

    private String determineTargetUrl(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        authorities.forEach(authority -> System.out.println("Granted Authority: " + authority.getAuthority()));  // ROLE_ADMIN 확인
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                return "/admin/dashboard";
            } else if (authority.getAuthority().equals("ROLE_WORKER")) {
                return "/worker/packaging";
            }
        }
        return "/login";
    }
}
