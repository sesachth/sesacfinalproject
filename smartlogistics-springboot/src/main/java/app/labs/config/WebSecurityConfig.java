package app.labs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

	private static final String[] STATIC_RESOURCE_PATHS = {"/css/**", "/js/**", "/json/**"};
	private static final String[] PUBLIC_URLS = {
		"/login",
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
	    http.authorizeHttpRequests((requests) -> requests
	            .requestMatchers(STATIC_RESOURCE_PATHS).permitAll()
	            .requestMatchers(PUBLIC_URLS).permitAll()
	            .anyRequest().authenticated()
	        );

	    return http.build();
	}
}
