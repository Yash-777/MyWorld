package com.github.yash777.myworld.aspects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Aspect for logging details of incoming HTTP requests to Spring controller methods.
 * This includes logging method signature, annotations, headers, parameters,
 * request body, and response payload with execution time.
 *
 * <p>Triggers on any controller methods annotated with Spring’s HTTP mapping annotations
 * like {@code @RequestMapping}, {@code @GetMapping}, etc.</p>
 *
 * <p><strong>Example Use Cases:</strong></p>
 * <ul>
 *   <li>Audit incoming requests with full HTTP context</li>
 *   <li>Debug unexpected behavior by logging payloads and headers</li>
 *   <li>Analyze API usage or integration testing</li>
 * </ul>
 *
 * <p><strong>Note:</strong> This should not be used in production without proper performance consideration
 * and sensitive data masking.</p>
 *
 * @author Yash
 */
@Component
@Aspect @Order(1) // This aspect executes before others with a higher order
@org.springframework.context.annotation.EnableAspectJAutoProxy
// If you're using @Aspect in any Spring AOP configuration or annotation-driven approach (@EnableAspectJAutoProxy), then AspectJ runtime must be available in the module's classpath.
public class RequestLoggingAspect {
	
	// Thread-local flag to be used by downstream aspects
	public static final ThreadLocal<Boolean> reqTriggered = ThreadLocal.withInitial(() -> false);
	
	/**
	 * Helper method to print details from a {@link RequestMapping} annotation.
	 *
	 * @param requestParam the RequestMapping annotation instance
	 */
	public void printRequestMappingParam(RequestMapping requestParam) {
		//org.springframework.web.bind.annotation.RequestMapping requestParam = AnnotationUtils.findAnnotation(method, org.springframework.web.bind.annotation.RequestMapping.class);
		if (requestParam != null) {// @RequestParam
			String nameOfMapping = requestParam.name();
			if (nameOfMapping != null && nameOfMapping.length() > 0) {
				System.out.println("nameOfMapping:"+nameOfMapping);
			}
			
			System.out.println("Headers: " + Arrays.toString(requestParam.headers()));
			System.out.println("Produces: " + Arrays.toString(requestParam.produces()));
			System.out.println("Consumes: " + Arrays.toString(requestParam.consumes()));
			System.out.println("RequestMethods: " + Arrays.toString(requestParam.method()));
			System.out.println("Params: " + Arrays.toString(requestParam.params()));
			System.out.println("Values: " + Arrays.toString(requestParam.value()));
		}
	}
	
	/**
	 * Pointcut that matches all HTTP mapping annotations on controller methods.
	 */
	@Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) " +
			"|| @annotation(org.springframework.web.bind.annotation.GetMapping) " +
			"|| @annotation(org.springframework.web.bind.annotation.PostMapping) " +
			"|| @annotation(org.springframework.web.bind.annotation.PathVariable) " +
			"|| @annotation(org.springframework.web.bind.annotation.PutMapping) " +
			"|| @annotation(org.springframework.web.bind.annotation.DeleteMapping)")
	public void requestMapping() {}
	
	/**
	 * Around advice to log request and response details of matched methods.
	 *
	 * @param joinPoint the join point representing the intercepted method
	 * @return the original method’s return value
	 * @throws Throwable if the intercepted method throws any exception
	 */
	@Around("requestMapping()")
	public Object logRequest(ProceedingJoinPoint joinPoint) throws Throwable {
		reqTriggered.set(true); // Enable the downstream aspect
		
		org.aspectj.lang.reflect.MethodSignature signature = (org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature();
		System.out.println("MethodSignature:"+ signature );
		
		long startTime = System.currentTimeMillis();
		
		Object result = null;
		try {
			// Log request details before method execution
			logRequestDetails(joinPoint);
			
			logDetails(joinPoint);
			
			// Proceed with the original method execution
			result = joinPoint.proceed();
			
			// Log response details after method execution
			long endTime = System.currentTimeMillis();
			logResponseDetails(result, endTime - startTime);
		} finally {
			reqTriggered.remove(); // IMPORTANT: Avoid memory leaks
		}
		
		return result;
	}
	
	/**
	 * Logs basic request information like method, URI, headers, query parameters, and request body.
	 */
	private void logRequestDetails(ProceedingJoinPoint joinPoint) throws IOException {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attributes != null) {
			HttpServletRequest request = attributes.getRequest();
			
			// Log request method, URI, headers, query parameters, and request body
			System.out.println("Request Method: " + request.getMethod());
			System.out.println("Request URI: " + request.getRequestURI());
			System.out.println("Request Headers: " + getRequestHeaders(request));
			
			System.out.println("Query String: " + request.getQueryString());
			System.out.println("Query Parameters: " + getRequestParameters(request));
			
			// Log request body in JSON format
			ServletInputStream inputStream = request.getInputStream();
			if (inputStream != null) {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
					Stream<String> lines = reader.lines();
					Optional<String> reduce = lines.reduce(String::concat);
					//String requestBody = reader.lines().collect(Collectors.joining());
					//if (!requestBody.isEmpty()) {
					if (reduce.isPresent()) { // NoSuchElementException: No value present at java.util.Optional.get(Optional.java:135)
						String requestBody = reduce.get();
						System.out.println("HttpServletRequest Body String: " + requestBody );
						try {
							ObjectMapper objectMapper = new ObjectMapper();
							JsonNode requestBodyJson = objectMapper.valueToTree(requestBody);
							System.out.println("HttpServletRequest Body JSON Node: " + requestBodyJson);
						} catch (Exception e) {
							System.err.println("Error parsing request body: " + e.getMessage());
						}
					}
				} catch (Exception ex) {
					System.err.println("Error reading request body: " + ex.getMessage());
				}
			}
		}
	}
	
	/**
	 * Logs details of annotations on the target method such as
	 * {@link RequestMapping}, {@link PathVariable}, and {@link RequestParam}.
	 */
	private void logDetails(ProceedingJoinPoint joinPoint) throws Throwable {
		// Access the HttpServletRequest
		System.out.println("----- logDetails -----");
		org.aspectj.lang.reflect.MethodSignature signature = (org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature();
		//System.out.println("MethodSignature:"+ signature );
		java.lang.reflect.Method method = signature.getMethod();
		
		// HttpComponentsClientHttpRequestFactory - method.getClass()
		// AnnotationFilter JAVA_LANG_ANNOTATION_FILTER = AnnotationFilter.packages("java.lang.annotation")
		
		//@org.springframework.web.bind.annotation.RequestMapping(path=[], headers=[], method=[GET], name=, produces=[], params=[], value=[compensation/ratecard/{planId}], consumes=[])
		Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
		for (Annotation annotation : declaredAnnotations) {
			System.out.println("Method DeclaredAnnotations:"+annotation.toString());
			//@org.springframework.security.access.prepost.PreAuthorize(value=hasPermission('/commissionrun/view', 'Feature Access'))
			//@org.springframework.web.bind.annotation.RequestMapping(path=[], headers=[], method=[GET], name=, produces=[], params=[], value=[commissionrundetails/{periodName}], consumes=[])
			
			if (annotation instanceof org.springframework.web.bind.annotation.RequestMapping) { 
				org.springframework.web.bind.annotation.RequestMapping requestParam = (RequestMapping) annotation;
				//org.springframework.web.bind.annotation.RequestMapping requestParam = AnnotationUtils.findAnnotation(method, org.springframework.web.bind.annotation.RequestMapping.class);
				System.out.println("Annotation - RequestMapping - name:val = " + requestParam.name() + " : " + requestParam.value());
				printRequestMappingParam(requestParam);
			}
		}
		
		/*
		 * it's important to note that @QueryParam is not a Spring annotation; it's typically used in JAX-RS (Java API for RESTful Web Services).
		 * In Spring, query parameters are usually handled using @RequestParam. Below is an example of a Spring Rest API method that accepts 
		 * @PathVariable, @RequestParam, and a query parameter (assuming it as @RequestParam): // @javax.ws.rs.QueryParam
		 */
		Annotation[] annotationsOnMethod = method.getAnnotations();
		for (Annotation annotation : annotationsOnMethod) {
			System.out.println("Method annotationsOnMethod:"+annotation.toString());
		}
		System.out.println("----- logDetails -----");
	}
	
	/**
	 * Logs the response and method execution time.
	 *
	 * @param result the method return value
	 * @param executionTime time taken to execute the method (in milliseconds)
	 */
	private void logResponseDetails(Object result, long executionTime) {
		System.out.println("Response: " + result);
		System.out.println("Execution Time: " + executionTime + " ms");
	}
	
	// Extra Methods
	/**
	 * Experimental method for logging detailed execution and annotation metadata.
	 */
	private Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
		// Access the HttpServletRequest
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			System.out.println("Header: " + headerNames.nextElement());
		}
		
		var signature = (org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature();
		System.out.println("MethodSignature:"+ signature );
		java.lang.reflect.Method method = signature.getMethod();
		
		// HttpComponentsClientHttpRequestFactory - method.getClass()
		// AnnotationFilter JAVA_LANG_ANNOTATION_FILTER = AnnotationFilter.packages("java.lang.annotation")
		
		//@org.springframework.web.bind.annotation.RequestMapping(path=[], headers=[], method=[GET], name=, produces=[], params=[], value=[compensation/ratecard/{planId}], consumes=[])
		Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
		//Annotation[] annotations = method.getAnnotations();
		for (Annotation annotation : declaredAnnotations) {
			System.out.println("annotation:"+annotation.toString());
			//@org.springframework.security.access.prepost.PreAuthorize(value=)
			if (annotation instanceof org.springframework.web.bind.annotation.PathVariable) {// @PathVariable
				org.springframework.web.bind.annotation.PathVariable pathVar = (PathVariable) annotation;
				//org.springframework.web.bind.annotation.PathVariable pathVariable = AnnotationUtils.findAnnotation(method, org.springframework.web.bind.annotation.PathVariable.class);
				System.out.println("Annotation - PathVariable - name:val = " + pathVar.name() + " : " + pathVar.value());
			}
			//Key: interface org.springframework.web.bind.annotation.RequestMapping
			//Value: @org.springframework.web.bind.annotation.RequestMapping(path=[], headers=[], method=[POST], name=, produces=[], params=[], value=[organizationalUnit], consumes=[])
			if (annotation instanceof org.springframework.web.bind.annotation.RequestParam) { // @javax.ws.rs.QueryParam
				org.springframework.web.bind.annotation.RequestParam requestPar = (RequestParam) annotation;
				System.out.println("Annotation - RequestParam - name:val = " + requestPar.name() + " : " + requestPar.value());
			}
			if (annotation instanceof org.springframework.web.bind.annotation.RequestMapping) { // @javax.ws.rs.QueryParam
				org.springframework.web.bind.annotation.RequestMapping requestParam = (RequestMapping) annotation;
				//org.springframework.web.bind.annotation.RequestMapping requestParam = AnnotationUtils.findAnnotation(method, org.springframework.web.bind.annotation.RequestMapping.class);
				System.out.println("Annotation - RequestMapping - name:val = " + requestParam.name() + " : " + requestParam.value());
				printRequestMappingParam(requestParam);
			}
		}
		
		Object result = forwordReq(joinPoint);
		return result;
	}
	/**
	 * Forwards the join point and measures execution time using {@link org.springframework.util.StopWatch}.
	 */	
	private Object forwordReq(ProceedingJoinPoint joinPoint) throws Throwable {
		//long startTime = System.currentTimeMillis();
		org.springframework.util.StopWatch sw = new org.springframework.util.StopWatch();
		sw.start("logExecutionTime:");
		
		Object result = joinPoint.proceed();
		
		sw.stop();
		long timeTaken = sw.getTaskInfo()[0].getTimeMillis(); //endTime = System.currentTimeMillis() - startTime;
		System.out.println("Timetaken in MilliSec:"+timeTaken);
		return result;
	}
	/**
	 * Extracts headers from the incoming request as a map.
	 *
	 * @param request the HTTP servlet request
	 * @return map of header names to values
	 */
	private Map<String, String> getRequestHeaders(HttpServletRequest request) {
		return Collections.list(request.getHeaderNames())
				.stream()
				.collect(Collectors.toMap(name -> name, request::getHeader));
	}
	/**
	 * Extracts query parameters from the incoming request as a map.
	 *
	 * @param request the HTTP servlet request
	 * @return map of parameter names to values (stringified)
	 */
	private Map<Object, Object> getRequestParameters(HttpServletRequest request) {
		return request.getParameterMap()
				.entrySet()
				.stream()
				.collect(Collectors.toMap(Map.Entry::getKey, entry -> Arrays.toString(entry.getValue())));
	}
}
