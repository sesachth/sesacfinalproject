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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    private static final String[] STATIC_RESOURCE_PATHS = {"/css/**", "/js/**", "/json/**", "/images/**"};
    private static final String[] PUBLIC_URLS = {
        "/login",
        "/admin-login",
        "/worker-login"
        "/admin/main",
        "/admin/product", 
        "/admin/order",
        "/admin/progress",
        "/admin/pallet", 
        "/admin/simulator",
        "/worker/packaging",
        "/worker/simulator", 
        "/common/check"
    };
  
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(STATIC_RESOURCE_PATHS).permitAll()
                .requestMatchers(PUBLIC_URLS).permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")  // 관리자 전용
                .requestMatchers("/worker/**").hasRole("WORKER") // 작업자 전용
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login") // 로그인 페이지 설정
                .defaultSuccessUrl("/admin/main", true) // 로그인 성공 시 이동
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .permitAll()
            );

        return http.build();
    }

    // 임시 사용자 정보 (실제 인증은 DB 연동 필요)
    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails admin = User.withDefaultPasswordEncoder()
            .username("admin")
            .password("admin")
            .roles("ADMIN")
            .build();
        
        UserDetails worker = User.withDefaultPasswordEncoder()
            .username("worker")
            .password("worker")
            .roles("WORKER")
            .build();
        
        return new InMemoryUserDetailsManager(admin, worker);
    }
    
    // AuthenticationProvider 설정
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        return provider;
    }

    // AuthenticationManager 설정
    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(authenticationProvider()));
    }
}
