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
        System.out.println("로그인 성공! 리디렉션 URL: " + targetUrl);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String determineTargetUrl(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority authority : authorities) {
        	 System.out.println("사용자 권한 확인: " + authority.getAuthority());
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                return "/admin/main";
            } else if (authority.getAuthority().equals("ROLE_WORKER")) {
                return "/worker/simulator";
            }
        }
        return "/login";
    }
}
