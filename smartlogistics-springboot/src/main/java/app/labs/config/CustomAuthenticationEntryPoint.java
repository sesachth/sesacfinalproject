package app.labs.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) 
            throws IOException, ServletException {

    	// 요청된 URL이 "/admin" 또는 "/worker"로 시작할 때만 팝업 띄우기
        String uri = request.getRequestURI();
        
        // 허용된 URL 목록 (직접 지정)
        List<String> allowedUrls = Arrays.asList(
            "/admin/dashboard", 
            "/admin/product", 
            "/admin/order", 
            "/admin/progress", 
            "/admin/pallet", 
            "/admin/stacking", 
            "/worker/packaging", 
            "/worker/stacking", 
            "/common/check"
        );
        
     // 요청된 URL이 allowedUrls일 때만 팝업 띄우기
        if (allowedUrls.contains(uri)) {
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(
                "<script>" +
                    "alert('로그인이 필요합니다. 로그인 페이지로 이동합니다.');" +
                    "window.location.href='/login';" +
                "</script>"
            );
            response.getWriter().flush();
        } else {
            // 404 처리하기 (경로가 잘못되었을 경우)
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}