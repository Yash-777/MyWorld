package com.github.yash777.myworld.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Custom implementation of Spring Security's {@link AuthenticationEntryPoint} that allows
 * returning a JSON error response instead of the default 401 Unauthorized error page.
 *
 * <p>
 * By default, Spring Security intercepts unauthorized HTTP requests and responds with a
 * plain 401 status code. This class allows you to send a structured JSON message
 * instead, which is especially helpful for frontend applications or APIs expecting JSON.
 * </p>
 *
 * <h3>How it works:</h3>
 * <ul>
 *   <li>Intercepts unauthorized access attempts to protected resources</li>
 *   <li>Returns a JSON response with HTTP status 401</li>
 *   <li>Provides a user-friendly error message</li>
 * </ul>
 *
 * <h3>Usage:</h3>
 * <pre>{@code
 * http
 *   .httpBasic()
 *   .authenticationEntryPoint(customAuthenticationEntryPoint);
 * }</pre>
 *
 * <p>Register this component in your Spring Security configuration using
 * {@code @Autowired} or constructor injection into your security filter chain.</p>
 *
 * @author Yash
 * @since 1.0
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
	
	/**
	 * Handles unauthorized access attempts by returning a custom JSON message instead of the default error page.
	 *
	 * @param request       the {@link HttpServletRequest}
	 * @param response      the {@link HttpServletResponse}
	 * @param authException the exception that caused the authentication failure
	 * @throws IOException if an input or output error occurs
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
		response.setContentType("application/json");
		response.getWriter().write("{ \"error\": \"You are not authorized to access this resource.\" }");
	}
}
