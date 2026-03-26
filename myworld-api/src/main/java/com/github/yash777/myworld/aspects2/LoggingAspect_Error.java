package com.github.yash777.myworld.aspects2;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

//@Aspect
//@Component
public class LoggingAspect_Error {
/*
❗ Real Problem Based on Stack Trace

java.lang.NullPointerException: Cannot invoke "org.apache.commons.logging.Log.isDebugEnabled()" because "this.logger" is null
	at org.springframework.web.filter.GenericFilterBean.init(GenericFilterBean.java:241) ~[spring-web-5.3.18.jar:5.3.18]
	
2025-09-29 14:47:46.824 ERROR 6776 --- [           main] o.a.c.c.C.[.[.[/]                        : Exception starting filter [inboundIntegrationFilter]

 */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LoggingAspect_Error.class);
    
	// protected final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LoggingAspect.class);
	// LOG.info("Thread: {}", threadName);
	
    private static final ThreadLocal<Boolean> trackMethodCalls = ThreadLocal.withInitial(() -> false);
    
    // Intercept all controller methods annotated with these mappings
    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String threadName = Thread.currentThread().getName();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        if (request != null) {
            String method = request.getMethod();
            String url = request.getRequestURL().toString();
            String queryString = request.getQueryString();
            String fullUrl = url + (queryString != null ? "?" + queryString : "");

            String className = joinPoint.getSignature().getDeclaringTypeName();
            String methodName = joinPoint.getSignature().getName();

            LOG.info("--------------------------------------------------");
            LOG.info("Thread: {}", threadName);
            LOG.info("Start Time: {}", startTime);
            LOG.info("Handler: {}#{}", className, methodName);
            LOG.info("HTTP Method: {}", method);
            LOG.info("Request URL: {}", fullUrl);

            // ✅ Capture all methods under these endpoints
            if (url.contains("/epretroactive/") || url.contains("/commissionrun/")) {
                LOG.info("⚡ Matched endpoint path: {}", url);
                // You can add additional logic here to capture request body, headers, etc.
                trackMethodCalls.set(true); // ✅ Enable tracking for this request
            }
        }

        Object result;
        try {
            result = joinPoint.proceed(); // Proceed with method execution
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            long minutes = duration / 60000;
            long seconds = (duration % 60000) / 1000;
            long millis = duration % 1000;

            LOG.info("Thread: {} Method: {} executed in {} min {} sec {} ms",
                    threadName, joinPoint.getSignature().getName(), minutes, seconds, millis);
            LOG.info("--------------------------------------------------");
            
            trackMethodCalls.remove(); // ✅ Prevent memory leaks in thread pool
        }

        return result;
    }
    
    
    
//    @Around("execution(* com.vorwerk.dspro..service..*(..)) || execution(* com.vorwerk.dspro..dao..*(..))")
    @Around("execution(* com.vorwerk.dspro..*(..))")
    public Object captureInternalMethodCalls(ProceedingJoinPoint joinPoint) throws Throwable {

        // Only log if the current thread is handling a tracked request
        if (!LoggingAspect_Error.trackMethodCalls.get()) {
            return joinPoint.proceed();
        }

        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;

        long minutes = duration / 60000;
        long seconds = (duration % 60000) / 1000;
//        LOG.info("Method Name: {} executed in {} min {} sec", joinPoint.getSignature().getName(), minutes, seconds);
        
        LOG.info("📍 Internal Method: {}#{} executed in {} min {} sec", className, methodName, minutes, seconds);
        return result;
    }
}



