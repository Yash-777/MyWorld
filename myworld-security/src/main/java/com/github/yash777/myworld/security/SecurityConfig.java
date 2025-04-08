package com.github.yash777.myworld.security;

import java.time.Duration;

import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.boot.web.servlet.server.Session;
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
	static boolean isHttpBasicSecurity = true;
	static boolean isBCryptPasswordEncoder = true;
	
	/**
	 * session timeout due to inactivity equivalent to setting `server.servlet.session.timeout=1m` in properties/yaml.
	 * @return
	 */
	@Bean
	public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> sessionTimeoutCustomizer() {
		return factory -> {
			Session session = new org.springframework.boot.web.servlet.server.Session();
			session.setTimeout(Duration.ofMinutes(5)); //  👈 ⏱️ minute timeout
			factory.setSession(session);
		};
		//return factory -> factory.setSessionTimeout(Duration.ofMinutes(1)); // 👈 1 minute
	}
	
	/** https://github.com/spring-projects/spring-security-samples/blob/main/servlet/spring-boot/java/hello-security-explicit/src/main/java/example/SecurityConfiguration.java
	 * <ul>Spring Security REST APIs - HTTP Basic Authentication
	 * <li>Accessing http://localhost:8080/myworld/slf4j/test/log → ✅ should work with no authentication.
	 * <li>Accessing http://localhost:8080/myworld/sample/text → 🔐 should prompt for username/password (admin:admin123).
	 * </ul>
	 * 
	 * <ul> HTTP headers
	 * <li> Request Header: Cookie: JSESSIONID=323BF62C2312509CF3F1432987534603
	 * <li> Response Header:Set-Cookie: JSESSIONID=2126EA5174A678BC7400D8CA3C9C1B61; Path=/myworld; HttpOnly
	 * </ul>
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// Spring Security matches from the root of the URL path - Security rule and Permit rule
		//org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration
		//.antMatchers().permitAll().anyRequest().authenticated() IllegalStateException: Can't configure antMatchers after anyRequest
		http.authorizeHttpRequests( (authorizeRequests) -> authorizeRequests
				.antMatchers("/login", "/logout", "/public/**").permitAll() // ⬅️ allow login without auth, exclude context-path=/myworld
				.antMatchers("/slf4j/**").permitAll()  // Allow direct access
				//.antMatchers("/myworld/slf4j/**").permitAll()  // exclude context-path to Allow direct access
				.anyRequest().authenticated()          // Everything else requires authentication
				);
		
		if (isHttpBasicSecurity) { // HttpBasicSecurityConfig - should prompt for username/password
			http.httpBasic();
		} else { // FormLoginSecurityConfig Default Login Page for username/password - http://localhost:8080/myworld/login
			http.formLogin() // ✅ this alone enables default login page
			//🔥 Important: DO NOT set .loginPage(...) unless you serve an HTML custom login page yourself.
			//.loginPage("/custom/loginpage") // ⬅️ only if you're serving a login page (optional)
			.loginProcessingUrl("/login") // exclude context-path=/myworld
			.successHandler((request, response, authentication) -> {
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("application/json");
				response.getWriter().write("{\"message\": \"Login successful\"}");
			})
			.failureHandler((request, response, exception) -> {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.setContentType("application/json");
				response.getWriter().write("{\"error\": \"Login failed\"}");
			})
			.and()
			.logout()
			.logoutUrl("/logout") // exclude context-path=/myworld
			.logoutSuccessHandler((request, response, authentication) -> {
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("application/json");
				response.getWriter().write("{\"message\": \"Logout successful\"}");
			})
//			.and()
//			.exceptionHandling()
//			.authenticationEntryPoint((request, response, authException) -> {
//				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//				response.setContentType("application/json");
//				response.getWriter().write("{\"error\": \"Unauthorized\", \"POST credentials to log in\": \"/myworld/login\"}");
//			})
			;
			
		}
		
		http.csrf().disable(); // For REST APIs, CSRF can be disabled
		
		return http.build();
	}
	
	
	@Bean
	public UserDetailsService userDetailsService() {
		//In-memory authentication with InMemoryUserDetailsManager
		var uds = new InMemoryUserDetailsManager();
		
		uds.createUser( getUser("admin", "admin123", "ADMIN") );
		uds.createUser( getUser("user", "password", "USER") );
		return uds;
	}
	private UserDetails getUser(String userName, String password, String... roles) {
		String userPassword = "{noop}"+password; // {noop} disables password encoding
		if (isBCryptPasswordEncoder) {
			userPassword = passwordEncoder().encode( password );
		}
		UserDetails user = User.withUsername( userName )
				.password(userPassword)
				.roles( roles )
				.build();
		
		return user;
	}
	
	//o.s.s.c.bcrypt.BCryptPasswordEncoder     : Encoded password does not look like BCrypt
	@Bean
	public PasswordEncoder passwordEncoder() {
		//BCrypt password encoding via BCryptPasswordEncoder
		return new BCryptPasswordEncoder();
	}
}
