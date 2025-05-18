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
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
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
	static boolean isHttpBasicSecurity = false; // FormLogin
	static boolean isBCryptPasswordEncoder = true;
	
	/**
	 * Indicates whether a custom HTML login page is used instead of Spring Security's default.
	 * Set to true only if you have your own login UI (e.g., mylogin.html).
	 */
	static boolean isCustomFormLogin = true;
	
	/**
	 * Path to the custom login page (GET handler).
	 * This should match the actual URL where your custom HTML login page is served.
	 *
	 * Example: src/main/resources/static/public/mylogin.html
	 * URL: http://localhost:8080/myworld/public/mylogin.html
	 */
	static String myLoginCustomUI_GET = "/public/mylogin.html";
	
	/**
	 * Path that handles the login form submission (POST handler).
	 * This must match the `action` attribute in your login HTML form.
	 *
	 * This is processed internally by Spring Security.
	 */
	static String myLoginPath_POSThandler = "/myloginpost";
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
				// Allow public access to login page and static resources
				.antMatchers("/public/**", "/css/**", "/js/**", "/images/**").permitAll() // ⬅️ allow login without auth, exclude context-path=/myworld
				.antMatchers(myLoginPath_POSThandler, "/logout").permitAll() // ⬅️ allow login without auth, exclude context-path=/myworld
				.antMatchers("/slf4j/**").permitAll()  // Allow direct access - // exclude context-path to Allow direct access
				
				// http://localhost:8080/myworld/swagger-ui.html -> {"error":"You are not authorized to access this resource."}
				// http://localhost:8080/myworld/swagger-ui/index.html - Swagger UI
				.antMatchers("/swagger-ui/**","/v3/api-docs/**", "/webjars/**", "/swagger-resources/**").permitAll()
				
				// All other endpoints require authentication
				.anyRequest().authenticated()          // Everything else requires authentication
				);
		
		// 2. Choose between HTTP Basic and Form Login
		if (isHttpBasicSecurity) { // HttpBasicSecurityConfig - Prompts basic browser login dialog for username/password
			http.httpBasic();
			//http.httpBasic(httpBasic -> httpBasic.authenticationEntryPoint(customAuthenticationEntryPoint));
		} else { // FormLoginSecurityConfig Default Login Page for username/password - http://localhost:8080/myworld/login
			// ✅ Enables default login page and allows browser access
			FormLoginConfigurer<HttpSecurity> formLogin = http.formLogin();
			
			processFormLogin(formLogin);
			
			formLogin
			.successHandler((request, response, authentication) -> {
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("application/json");
				
				response.getWriter().write("{\"message\": \"Login successful\"}");
				
				if (!customAuthenticationEntryPoint.isRestClient(request)) {
					// 🔁 Redirect for browser clients (Swagger UI:http://localhost:8080/myworld/swagger-ui/index.html)
					response.sendRedirect(request.getContextPath() + "/swagger-ui/index.html");
				}
			})
			.failureHandler((request, response, exception) -> {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.setContentType("application/json");
				response.getWriter().write("{\"error\": \"Login failed\"}");
				
				if (!customAuthenticationEntryPoint.isRestClient(request)) {
					// 🔁 Redirect for browser clients (Logout Page)
					response.sendRedirect(request.getContextPath() + "/public/mylogout.html");
				}
			});
			
			LogoutConfigurer<HttpSecurity> logout = http.logout();
			logout.permitAll() // Allow everyone to access the logout page
			.logoutUrl("/logout") // exclude context-path=/myworld
			.logoutSuccessHandler((request, response, authentication) -> {
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("application/json");
				response.getWriter().write("{\"message\": \"Logout successful\"}");
			})
			;
		}
		
		// 🔐 Configure Spring Security to use a custom AuthenticationEntryPoint
		// This overrides the default login page redirect behavior and delegates handling
		// of unauthorized (401) access attempts to `CustomAuthenticationEntryPoint`.
		// For REST clients, this typically returns JSON; for browser clients, it can redirect to a login page.
		http.exceptionHandling(configurer ->
			configurer.authenticationEntryPoint(customAuthenticationEntryPoint)
				);
		
		// CSRF protection: allow login POST without CSRF for tools like Postman [403-Forbidden - ✅ Postman logins via /login POST (API-style)]
		// 🔒 Important: Never disable CSRF in production without understanding the risks.
		//http.csrf().disable(); // For REST APIs, CSRF can be disabled
		http.csrf().ignoringAntMatchers(myLoginPath_POSThandler);
		
		return http.build();
	}
	
	/**
	 * Configures Spring Security's form login mechanism based on whether a custom login UI is used.
	 * This method enables either Spring Security's default login page or a custom HTML login form,
	 * and sets up the login POST handler accordingly.
	 *
	 * <p><strong>🚀 Custom Login Setup (Optional):</strong></p>
	 * <ul>
	 *   <li>If you're using a custom login page (e.g., {@code mylogin.html}), configure both:</li>
	 *   <ul>
	 *     <li>{@code .loginPage("/public/mylogin.html")} — path to the custom login UI</li>
	 *     <li>{@code .loginProcessingUrl("/myloginpost")} — form action for login POST</li>
	 *   </ul>
	 *   <li>Otherwise, Spring Security will show its default login page and handle POST at {@code /login}</li>
	 * </ul>
	 *
	 * <p><strong>📁 File Placement:</strong></p>
	 * <ul>
	 *   <li>Place {@code mylogin.html} inside: <code>src/main/resources/static/public/mylogin.html</code></li>
	 *   <li>This ensures Spring Boot serves the file as a static asset at <code>/public/mylogin.html</code></li>
	 * </ul>
	 *
	 * <p><strong>🔓 Static Resource Access Configuration:</strong></p>
	 * <ul>
	 *   <li>Permit access to static and public files in your Security config:</li>
	 *   <pre>
	 *   .antMatchers("/public/**", "/css/**", "/js/**", "/images/**").permitAll()
	 *   </pre>
	 * </ul>
	 *
	 * <p><strong>🐞 Common Redirect Errors (Explained):</strong></p>
	 * <ul>
	 *   <li>{@code SessionManagementFilter: Request requested invalid session id ...}</li>
	 *   <li>{@code HttpSessionRequestCache: Saved request http://localhost:8080/myworld/sample/text}</li>
	 *   <li>{@code DefaultRedirectStrategy: Redirecting to /myworld/public/mylogin.html}</li>
	 *   <li>This typically happens when the session expires or an unauthenticated user accesses a protected page.</li>
	 * </ul>
	 *
	 * <p><strong>🔧 Notes on Custom Form:</strong></p>
	 * <ul>
	 *   <li>In your HTML form, make sure:</li>
	 *   <ul>
	 *     <li>{@code <form action="/myloginpost" method="post">}</li>
	 *     <li>This path must match {@code .loginProcessingUrl("/myloginpost")}</li>
	 *   </ul>
	 *   <li>When the login button is clicked, Spring will handle the POST request:</li>
	 *   <pre>
	 *   POST http://localhost:8080/myworld/myloginpost
	 *   </pre>
	 * </ul>
	 *
	 * @param formLogin the FormLoginConfigurer to configure login behavior
	 */
	private void processFormLogin(FormLoginConfigurer<HttpSecurity> formLogin) {
		if (isCustomFormLogin) {
			// ✅ Set the GET endpoint for the login page (served by Spring Boot as a static file)
			//🔥 Important: DO NOT set .loginPage(...) unless you serve an HTML custom login page yourself.
			formLogin.loginPage(myLoginCustomUI_GET); // ⬅️ only if you're serving a login page (optional)
		}
		//http.formLogin(withDefaults()); ✅ Only works in Spring Security 6+ (i.e., Spring Boot 3+)
		
		// ✅ POST endpoint for login form submission : This must match the `action="/myloginpost"` in your HTML login form
		formLogin.loginProcessingUrl(myLoginPath_POSThandler); // exclude context-path=/myworld
		
		// ✅ Allow everyone to access the login page and login POST handler
		formLogin.permitAll();
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
