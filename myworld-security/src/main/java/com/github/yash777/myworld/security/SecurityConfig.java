package com.github.yash777.myworld.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
            	.antMatchers("/login").authenticated()  // Protect /login
            	.antMatchers("/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic() // Enable HTTP Basic Authentication  - // or formLogin(), jwt(), etc.
            .and()
            .csrf().disable(); // Disable CSRF for testing APIs easily (not recommended for prod)
        
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        var uds = new InMemoryUserDetailsManager();
        
        UserDetails build = User.withUsername("admin")
             //.password("{noop}admin123") // {noop} disables password encoding
             .password(passwordEncoder().encode("admin123"))
             .roles("ADMIN") // USER
             .build();
        
        uds.createUser(build);
        return uds;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
