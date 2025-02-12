package app.labs.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
        "/admin-login",
        "/worker-login",
        "/admin/main",
        "/admin/product", 
        "/admin/order",
        "/admin/progress",
        "/admin/pallet", 
        "/admin/simulator",
        "/worker/packaging",
        "/worker/simulator", 
        "/common/check",
    };
  
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin/order/generate").permitAll() // ✅ 주문 생성 API 접근 허용
                .requestMatchers("/admin/order/**").permitAll() // ✅ 필요시 전체 허용 가능
                .requestMatchers(STATIC_RESOURCE_PATHS).permitAll()
                .requestMatchers(PUBLIC_URLS).permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")  
                .requestMatchers("/worker/**").hasAnyRole("ADMIN", "WORKER") 
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
            	.loginPage("/login")
                .loginProcessingUrl("/perform_login") // 로그인 처리 URL
                .usernameParameter("username")  // HTML input name과 일치해야 함
                .passwordParameter("password")
                .successHandler(new CustomLoginSuccessHandler()) // 로그인 성공 핸들러
                .permitAll()	
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .permitAll()
            )
            .exceptionHandling(exception -> exception
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
