package app.labs.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    private static final String[] STATIC_RESOURCE_PATHS = {"/css/**", "/js/**", "/json/**", "/images/**"};
    private static final String[] PUBLIC_URLS = {
        "/login",
        "/admin/dashboard",
        "/admin/product", 
        "/admin/order",
        "/admin/progress",
        "/admin/pallet", 
        "/admin/stacking",
        "/worker/packaging",
        "/worker/stacking", 
        "/admin/check",
    };
  
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, CustomAuthenticationEntryPoint authEntryPoint) throws Exception {
        http
        	.csrf(csrf -> csrf  // spring security는 기본적으로 CSRF 공격을 보호함
            .disable()) 
            .authorizeHttpRequests(auth -> auth
                //.requestMatchers("/admin/order/generate").permitAll() // ✅ 주문 생성 API 접근 허용
                //.requestMatchers("/admin/order/**").permitAll() // ✅ 필요시 전체 허용 가능
            	.requestMatchers("/api/v1/**").permitAll()  // API 엔드포인트 허용
                .requestMatchers("/ws/**").permitAll()	
            	.requestMatchers("/login").permitAll()
            	.requestMatchers(STATIC_RESOURCE_PATHS).permitAll()
                //.requestMatchers(PUBLIC_URLS).permitAll()
            	.requestMatchers("/admin/dashboard", "/admin/product", "/admin/order", "/admin/progress", "/admin/pallet", "/admin/stacking", "/admin/check").hasRole("ADMIN")  
            	.requestMatchers("/worker/packaging", "/worker/stacking").hasAnyRole("ADMIN", "WORKER") 
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
            	.loginPage("/login")
                .loginProcessingUrl("/perform_login") // 로그인 처리 URL
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(new CustomLoginSuccessHandler()) // 로그인 성공 핸들러
                .failureHandler(new CustomLoginFailureHandler()) // 로그인 실패 핸들러
                .permitAll()	
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true) // 세션 무효화
                .deleteCookies("JSESSIONID") // 쿠키 삭제
                // 캐시 삭제
                .addLogoutHandler((request, response, authentication) -> {
                    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                    response.setHeader("Pragma", "no-cache");
                    response.setHeader("Expires", "0");
                    
                    // 보안 컨텍스트 초기화
                    SecurityContextHolder.clearContext();
                })
                .permitAll()
            )
            .exceptionHandling(exception -> exception
            	.authenticationEntryPoint(authEntryPoint) // 비로그인 유저 처리 추가
                .accessDeniedHandler(new CustomAccessDeniedHandler())
            );

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    // AuthenticationProvider 설정
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder()); // 패스워드 암호화 적용
        return provider;
    }

    // AuthenticationManager 설정
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider authenticationProvider) {
        return new ProviderManager(List.of(authenticationProvider));
    }
}
