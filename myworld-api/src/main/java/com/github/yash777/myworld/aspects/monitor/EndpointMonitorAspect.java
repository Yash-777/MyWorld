package com.github.yash777.myworld.aspects.monitor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.stream.IntStream;

//@Aspect
//@Component
//@EnableAspectJAutoProxy
public class EndpointMonitorAspect {
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) " +
			"|| @annotation(org.springframework.web.bind.annotation.GetMapping)" +
			"|| @annotation(org.springframework.web.bind.annotation.PostMapping)" +
			"|| @annotation(org.springframework.web.bind.annotation.PathVariable)" +
			"|| @annotation(org.springframework.web.bind.annotation.PutMapping)" +
			"|| @annotation(org.springframework.web.bind.annotation.DeleteMapping)"
			)
	//@Around("execution(* com.yourpackage.controller..*(..))")
	public Object logEndpointExecution(ProceedingJoinPoint joinPoint) throws Throwable {
		long startTime = System.currentTimeMillis();
		String threadName = Thread.currentThread().getName();
		
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
		
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		String className = methodSignature.getDeclaringType().getName();
		String methodName = methodSignature.getName();
		String[] paramNames = methodSignature.getParameterNames();
		Object[] paramValues = joinPoint.getArgs();
		
		StringBuilder methodParams = new StringBuilder();
		IntStream.range(0, paramNames.length).forEach(i -> {
			methodParams.append(paramNames[i]).append("=");
			methodParams.append(paramValues[i]);
			if (i < paramNames.length - 1) methodParams.append(", ");
		});
		
		Object response = null;
		Throwable error = null;
		
		try {
			response = joinPoint.proceed();
			return response;
		} catch (Throwable ex) {
			error = ex;
			throw ex;
		} finally {
			long duration = System.currentTimeMillis() - startTime;
			
			System.out.println("===== Endpoint Execution Trace ===== Thread: " + threadName);
			System.out.println("  → Controller: " + className);
			System.out.println("  → Method: " + methodName + "(" + methodParams + ")");
			
			if (request != null) {
				System.out.println("  → HTTP Method: " + request.getMethod());
				System.out.println("  → URL: " + request.getRequestURL() +
						(request.getQueryString() != null ? "?" + request.getQueryString() : ""));
				
				// Query parameters
				Enumeration<String> paramNamesEnum = request.getParameterNames();
				if (paramNamesEnum.hasMoreElements()) {
					System.out.println("  → Query Parameters:");
					while (paramNamesEnum.hasMoreElements()) {
						String name = paramNamesEnum.nextElement();
						System.out.println("     - " + name + ": " + request.getParameter(name));
					}
				}
				
				// Headers
				Enumeration<String> headerNames = request.getHeaderNames();
				if (headerNames.hasMoreElements()) {
					System.out.println("  → Headers:");
					while (headerNames.hasMoreElements()) {
						String header = headerNames.nextElement();
						System.out.println("     - " + header + ": " + request.getHeader(header));
					}
				}
				
				// Cookies
				Cookie[] cookies = request.getCookies();
				if (cookies != null && cookies.length > 0) {
					System.out.println("  → Cookies:");
					for (Cookie cookie : cookies) {
						System.out.println("     - " + cookie.getName() + ": " + cookie.getValue());
					}
				}
				
				// Request body (cached from filter)
				String rawBody = (String) request.getAttribute("cachedRequestBody");
				if (rawBody != null && !rawBody.isBlank()) {
					if (isJsonString(rawBody)) {
						String minified = objectMapper.writeValueAsString(
								objectMapper.readValue(rawBody, Object.class));
						System.out.println("  → Request Body Minified: " + minified);
					} else {
						System.out.println("  → Request Body: " + rawBody);
					}
				}
			}
			
			// Response
			if (response != null) {
				if (isJsonString(response.toString())) {
					String minified = objectMapper.writeValueAsString(
							objectMapper.readValue(response.toString(), Object.class));
					System.out.println("  → Response: " + minified);
				} else {
					System.out.println("  → Response: " + response);
				}
			} else if (error != null) {
				System.out.println("  → Response: Exception - " + error.getClass().getSimpleName() + ": " + error.getMessage());
			} else {
				System.out.println("  → Response: null");
			}
			
			System.out.println("  → Duration: " + duration + " ms");
			System.out.println("====================================");
		}
	}
	
	private boolean isJsonString(String input) {
		input = input.trim();
		if ((input.startsWith("{") && input.endsWith("}")) || (input.startsWith("[") && input.endsWith("]"))) {
			try {
				objectMapper.readTree(input);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}
}
