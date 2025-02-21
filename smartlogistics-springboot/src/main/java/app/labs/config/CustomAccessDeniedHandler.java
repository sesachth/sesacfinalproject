package app.labs.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_WORKER"))) {
            // 작업자가 관리자 페이지에 접근했을 때
            String requestedUrl = request.getRequestURI(); // 현재 요청된 URL을 가져옵니다.
            
            // 관리자 페이지 URL 목록
            List<String> adminUrls = Arrays.asList(
                "/admin/dashboard", 
                "/admin/product", 
                "/admin/order", 
                "/admin/progress", 
                "/admin/pallet", 
                "/admin/stacking",
                "/admin/check"
            );

            if (adminUrls.contains(requestedUrl)) {
                // 작업자 페이지로 리다이렉트할 URL 설정 (현재 작업자 페이지에 따라 다르게 설정)
                String redirectUrl = "/worker/packaging"; // 기본적으로 패키징 페이지로 설정

                // 요청된 URL이 /worker/stacking이면 /worker/stacking으로 리다이렉트
                if (requestedUrl.contains("/stacking")) {
                    redirectUrl = "/worker/stacking";
                }

                // 팝업 띄우기
                response.setContentType("text/html; charset=UTF-8");
                response.getWriter().write(
                    "<script>" +
                        "alert('관리자 계정만 접근 가능합니다.');" +
                        "window.location.href='" + redirectUrl + "';" +  // 작업자 페이지로 리다이렉트
                    "</script>"
                );
                response.getWriter().flush();
            } else {
                // 기본 403 처리 (관리자 페이지가 아닌 다른 URL 접근)
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            }
        }
    }
}
