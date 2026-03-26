package com.github.yash777.myworld.aspects;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

//@Aspect @Order(1) // This aspect executes before others with a higher order
//@Component
//@EnableAspectJAutoProxy
public class ApiEndpointTimeAspect {
	// Thread-local flag to be used by downstream aspects
	public static final ThreadLocal<Boolean> reqTriggered = ThreadLocal.withInitial(() -> false);
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) " +
			"|| @annotation(org.springframework.web.bind.annotation.GetMapping)" +
			"|| @annotation(org.springframework.web.bind.annotation.PostMapping)" +
			"|| @annotation(org.springframework.web.bind.annotation.PathVariable)" +
			"|| @annotation(org.springframework.web.bind.annotation.PutMapping)" +
			"|| @annotation(org.springframework.web.bind.annotation.DeleteMapping)"
			)
	public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
		long startTime = System.currentTimeMillis();
		String threadName = Thread.currentThread().getName();
		
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attributes != null) {
			HttpServletRequest request = attributes.getRequest();
			
			String method = request.getMethod();
			String url = request.getRequestURL().toString();
			String queryString = request.getQueryString();
			String fullUrl = url + (queryString != null ? "?" + queryString : "");
			
			String className = joinPoint.getSignature().getDeclaringTypeName();
			String methodName = joinPoint.getSignature().getName();
			
			System.out.println("--------------------------------------------------");
			System.out.println("Thread: " + threadName);
			System.out.println("Start Time: " + startTime);
			System.out.println("Handler: " + className + "#" + methodName);
			System.out.println("HTTP Method: " + method);
			System.out.println("Request URL: " + fullUrl);
			
			if (url.contains("/epretroactive/") || url.contains("/commissionrun/")) {
				System.out.println("Matched path: " + url);
				//reqTriggered.set(true); // Enable the downstream aspect
			}
			
			// Print Query Parameters
			Map<String, String> queryParams = new HashMap<>();
			Enumeration<String> paramNames = request.getParameterNames();
			while (paramNames.hasMoreElements()) {
				String param = paramNames.nextElement();
				queryParams.put(param, request.getParameter(param));
			}
			if (!queryParams.isEmpty()) {
				System.out.println("Query Parameters: " + objectMapper.writeValueAsString(queryParams));
			}
			
			// Raw JSON body for POST/PUT/DELETE
			if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method)) {
				String rawBody = (String) request.getAttribute("cachedRequestBody");
				if (rawBody != null && !rawBody.isBlank()) {
					printJSON(rawBody.toString());
				} else {
					System.out.println("Request Body: [Empty or missing]");
				}
			}
		}
		
		Object result;
		try {
			// ---- Proceed and capture response ----
			result = joinPoint.proceed();
			
			// ---- Log Response ----
			if (result != null) {
				printJSON(result.toString());
			} else {
				System.out.println("Response Body: [null]");
			}
		} finally {
			long endTime = System.currentTimeMillis();
			System.out.println("End Time: " + endTime);
			System.out.println("Thread: " + threadName + " | Duration: " + (endTime - startTime) + " ms");
			System.out.println("--------------------------------------------------");
			
			reqTriggered.remove(); // IMPORTANT: Avoid memory leaks
		}
		
		return result;
	}
	
	
	private void printJSON(String rawBody) {
		if (isJsonString(rawBody)) {
			try {
				Object json = objectMapper.readValue(rawBody, Object.class);
				
				// Minified JSON
				String minifiedJson = objectMapper.writeValueAsString(json);
				System.out.println("Minified JSON Body: " + minifiedJson);
				
				// Beautified JSON
				String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
				System.out.println("Beautified JSON Body:\n" + prettyJson);
			} catch (Exception e) {
				System.out.println("Failed to parse JSON body: " + rawBody);
			}
		} else {
			System.out.println("Raw Body (Non-JSON): " + rawBody);
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