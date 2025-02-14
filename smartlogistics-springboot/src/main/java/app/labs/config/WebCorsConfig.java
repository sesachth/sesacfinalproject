package app.labs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebCorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns(
                            "http://localhost",
                            "http://127.0.0.1",
                            "http://localhost:8000",  // ✅ FastAPI (8000번 포트)
                            "http://localhost:8081"   // ✅ WebSocket (8081번 포트)
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // ✅ OPTIONS 추가
                        .allowedHeaders("*")
                        .allowCredentials(true);  // ✅ allowCredentials(true) 설정 시, "*" 금지 → 명확한 도메인 패턴 사용
            }
        };
    }
}
