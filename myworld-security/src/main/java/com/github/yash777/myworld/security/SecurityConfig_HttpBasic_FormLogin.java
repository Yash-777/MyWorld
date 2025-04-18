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

/**
 * Spring Security configuration supporting both HTTP Basic and Form Login authentication mechanisms.
 * 
 * <p>This configuration secures endpoints using Spring Security's {@code SecurityFilterChain} and 
 * demonstrates how to:
 * <ul>
 *     <li>Define public and protected URL patterns</li>
 *     <li>Configure custom login/logout success and failure handlers</li>
 *     <li>Use a custom {@link CustomAuthenticationEntryPoint} to handle unauthorized access</li>
 *     <li>Enable in-memory user management with optional BCrypt encoding</li>
 *     <li>Configure session timeout using a {@code WebServerFactoryCustomizer}</li>
 * </ul>
 * </p>
 * 
 * <p>Example usage:
 * <ul>
 *   <li><code>GET /myworld/slf4j/test/log</code> → Public, no authentication</li>
 *   <li><code>GET /myworld/sample/text</code> → Requires authentication (admin:admin123)</li>
 * </ul>
 * </p>
 * 
 * @author Yash
 * @since 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig_HttpBasic_FormLogin {
	static boolean isHttpBasicSecurity = true;
	static boolean isBCryptPasswordEncoder = true;
	
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
	/**
	 * Constructor-based injection for custom authentication entry point.
	 * 
	 * @param customEntryPoint The custom authentication entry point bean.
	 */
	public SecurityConfig_HttpBasic_FormLogin(CustomAuthenticationEntryPoint customEntryPoint) {
		this.customAuthenticationEntryPoint = customEntryPoint;
	}
	
	/**
	 * Configures session timeout (inactivity-based) for the embedded servlet container.
	 * Equivalent to setting {@code server.servlet.session.timeout=5m} in application properties.
	 *
	 * @return a {@link WebServerFactoryCustomizer} that sets the session timeout to 5 minutes
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
	
	/**
	 * Configures the Spring Security {@link SecurityFilterChain}, specifying access control,
	 * authentication mechanisms (HTTP Basic or Form Login), session handling, and custom responses.
	 *
	 * <p>This method defines which endpoints are publicly accessible and which require authentication.
	 * It also configures custom login and logout handlers and integrates a custom {@link CustomAuthenticationEntryPoint}
	 * for returning JSON responses on unauthorized access.</p>
	 *
	 * <p><b>Sample Behavior:</b></p>
	 * <ul>
	 *   <li><code>GET /myworld/slf4j/test/log</code> → ✅ Public endpoint, accessible without authentication.</li>
	 *   <li><code>GET /myworld/sample/text</code> → 🔐 Requires authentication (e.g., <code>admin:admin123</code>).</li>
	 * </ul>
	 *
	 * <p><b>HTTP Session Example:</b></p>
	 * <ul>
	 *   <li><b>Request Header:</b> <code>Cookie: JSESSIONID=323BF62C2312509CF3F1432987534603</code></li>
	 *   <li><b>Response Header:</b> <code>Set-Cookie: JSESSIONID=2126EA5174A678BC7400D8CA3C9C1B61; Path=/myworld; HttpOnly</code></li>
	 * </ul>
	 *
	 * @param http the {@link HttpSecurity} configuration object provided by Spring Security
	 * @return a configured {@link SecurityFilterChain} bean that controls HTTP security behavior
	 * @throws Exception if a configuration error occurs
	 * @see <a href="https://github.com/spring-projects/spring-security-samples/blob/main/servlet/spring-boot/java/hello-security-explicit/src/main/java/example/SecurityConfiguration.java">
	 *      Spring Security Hello Sample</a>
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// 1. Define access rules
		// Spring Security matches from the root of the URL path - Security rule and Permit rule
		//org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration
		//.antMatchers().permitAll().anyRequest().authenticated() IllegalStateException: Can't configure antMatchers after anyRequest
		http.authorizeHttpRequests( (authorizeRequests) -> authorizeRequests
				.antMatchers("/login", "/logout", "/public/**").permitAll() // ⬅️ allow login without auth, exclude context-path=/myworld
				.antMatchers("/slf4j/**").permitAll()  // Allow direct access
				//.antMatchers("/myworld/slf4j/**").permitAll()  // exclude context-path to Allow direct access
				.anyRequest().authenticated()          // Everything else requires authentication
				);
		
		// 2. Choose between HTTP Basic and Form Login
		if (isHttpBasicSecurity) { // HttpBasicSecurityConfig - Prompts basic browser login dialog for username/password
			http.httpBasic();
			//http.httpBasic(httpBasic -> httpBasic.authenticationEntryPoint(customAuthenticationEntryPoint));
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
			});
			;
		}
		
		// Global handler for unauthenticated requests
		http.exceptionHandling(configurer ->
		configurer.authenticationEntryPoint(customAuthenticationEntryPoint)
				);
		
		// Disable CSRF for stateless API use cases
		http.csrf().disable(); // For REST APIs, CSRF can be disabled
		
		return http.build();
	}
	
	
	/**
	 * In-memory user store configuration. Defines two users:
	 * <ul>
	 *     <li><strong>admin</strong> / <em>admin123</em> → role: ADMIN</li>
	 *     <li><strong>user</strong> / <em>password</em> → role: USER</li>
	 * </ul>
	 *
	 * @return the configured {@link UserDetailsService}
	 */
	@Bean
	public UserDetailsService userDetailsService() {
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
		manager.createUser(createUser("admin", "admin123", "ADMIN"));
		manager.createUser(createUser("user", "password", "USER"));
		return manager;
	}
	
	/**
	 * Helper method to create a {@link UserDetails} instance.
	 *
	 * @param username the username
	 * @param password the plain text password
	 * @param roles    one or more roles assigned to the user
	 * @return the constructed {@link UserDetails}
	 */
	private UserDetails createUser(String username, String password, String... roles) {
		String encodedPassword = isBCryptPasswordEncoder
				? passwordEncoder().encode(password)
						: "{noop}" + password; // no-op encoding for testing, {noop} disables password encoding
		
		return User.withUsername(username)
				.password(encodedPassword)
				.roles(roles)
				.build();
	}
	
	/**
	 * Password encoder bean using BCrypt hashing algorithm.
	 * 
	 * @return an instance of {@link BCryptPasswordEncoder}
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
