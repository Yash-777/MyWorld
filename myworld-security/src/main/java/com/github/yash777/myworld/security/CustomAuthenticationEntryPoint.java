package com.github.yash777.myworld.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
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
/**
 * @author srika
 *
 */
/**
 * @author srika
 *
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
	
	/**
	 * Handles unauthorized access attempts by returning a 401 JSON response for REST clients,
	 * or redirecting to a custom login page for browser-based clients.
	 *
	 * <p><strong>Spring Security Behavior:</strong></p>
	 * <p>
	 * 🔥 <strong>Important:</strong> Once you configure a custom {@link AuthenticationEntryPoint},
	 * Spring Security will <em>not</em> automatically redirect to the login page anymore.
	 * </p>
	 *
	 * <p><strong>Why?</strong></p>
	 * <ul>
	 *   <li>By default, Spring Security redirects to the login page when:</li>
	 *   <ul>
	 *     <li>An unauthenticated user accesses a protected resource</li>
	 *     <li>No {@code AuthenticationEntryPoint} is explicitly set</li>
	 *   </ul>
	 *   <li>However, when you register a custom {@code AuthenticationEntryPoint},
	 *   you override this default behavior. Your implementation becomes responsible for what happens.</li>
	 *   <li>This is ideal for REST APIs that expect JSON responses instead of HTML redirects.</li>
	 * </ul>
	 *
	 * <p><strong>Behavior in this implementation:</strong></p>
	 * <ul>
	 *   <li>Returns a JSON response with HTTP 401 for REST/API clients like Postman or curl</li>
	 *   <li>Redirects browser-based clients to the custom login page (e.g., {@code /public/mylogin.html})</li>
	 * </ul>
	 *
	 * @param request       the incoming HTTP request
	 * @param response      the HTTP response to modify
	 * @param authException the authentication exception indicating the user is unauthorized
	 * @throws IOException if an input or output exception occurs
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
		
		String acceptHeader = request.getHeader("Accept");
		String xhrHeader = request.getHeader("X-Requested-With");
		String swaggerReferer = request.getHeader("referer");
		System.out.println("swaggerReferer :"+swaggerReferer);
		String requestURI = request.getRequestURI();
		System.out.println("requestURI :"+requestURI);
		
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			System.out.println("Cookie Name:"+ cookie.getName() +", Value:"+cookie.getValue());
		}
		boolean isSwaggerRequest = requestURI != null && (requestURI.contains("/swagger-ui") || requestURI.contains("/v3/api-docs")); // Swagger UI/API calls
		// Detect API or Swagger requests (i.e., non-browser use cases)
		boolean isApiRequest = isSwaggerRequest ||
				(swaggerReferer != null && swaggerReferer.contains("/swagger-ui/index.html")) || // http://localhost:8080/myworld/swagger-ui/index.html
				(acceptHeader != null && acceptHeader.contains("application/json")) ||
				(xhrHeader != null && "XMLHttpRequest".equalsIgnoreCase(xhrHeader)) ||
				(isRestClient(request));
		
		if (isApiRequest) {
			// 🔧 JSON response for REST clients(APIs or AJAX) or Swagger
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
			response.setContentType("application/json");
			response.getWriter().write("{ \"error\": \"You are not authorized to access this resource.\" }");
		} else {
			// 🔁 Redirect for browser clients (form-based login)
			response.sendRedirect(request.getContextPath() + "/public/mylogin.html");
		}
	}
	
	
	/**
	 * Detects whether the given User-Agent header value likely belongs to a REST client
	 * rather than a browser. This method is used to determine if the request should receive
	 * a JSON error response (e.g., Postman, curl, Java clients) instead of an HTML redirect.
	 *
	 * <p><strong>Common REST client User-Agent examples:</strong></p>
	 * <ul>
	 *   <li><strong>Postman</strong>: {@code PostmanRuntime/7.32.2}</li>
	 *   <li><strong>curl</strong>: {@code curl/7.74.0}</li>
	 *   <li><strong>Java</strong>: {@code Java/1.8.0_321} or {@code Apache-HttpClient/4.5.13}</li>
	 *   <li><strong>OkHttp</strong>: {@code okhttp/3.12.1}</li>
	 * </ul>
	 *
	 * <p><strong>Browser User-Agent examples:</strong></p>
	 * <ul>
	 *   <li>{@code Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/119.0.0}</li>
	 * </ul>
	 *
	 * @param request the incoming HTTP request
	 * @return true if the User-Agent appears to be a REST client, false if it's likely a browser
	 */
	public boolean isRestClient(HttpServletRequest request) {
		//User-Agent header string from the request
		String userAgent = request.getHeader("User-Agent");
		if (userAgent == null) return false;
		
		userAgent = userAgent.toLowerCase();
		
		return userAgent.contains("postman") || userAgent.contains("PostmanRuntime") ||
				userAgent.contains("curl") ||
				userAgent.contains("httpclient") || userAgent.contains("HttpClient") ||
				userAgent.contains("java") ||      // Apache HttpClient
				userAgent.contains("okhttp");      // Android REST clients
	}

}
