package com.github.yash777.myworld.aspects2;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Component @Aspect @org.springframework.core.annotation.Order(1)
@org.springframework.context.annotation.EnableAspectJAutoProxy
public class ControllerLoggingAspect {

    private static final Logger LOG = LoggerFactory.getLogger(ControllerLoggingAspect.class);

//    static final ThreadLocal<Boolean> trackMethodCalls = ThreadLocal.withInitial(() -> false);

    // Advice to wrap controller methods
    @Around("within(@org.springframework.web.bind.annotation.RestController *)"
    		//📍 Intercepted: org.springdoc.webmvc.api.OpenApiWebMvcResource#openapiJson, Thread:http-nio-8080-exec-10
    		+"&& !within(org.springdoc.webmvc.api..*) "
    		+"&& !within(org.springdoc.webmvc.ui..*) "
//    		📍 Intercepted: org.springdoc.webmvc.ui.SwaggerConfigResource#openapiJson, Thread:http-nio-8080-exec-10
    		)
    
    public Object logControllerExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    	String className = joinPoint.getSignature().getDeclaringTypeName();
    	String methodName = joinPoint.getSignature().getName();
    	System.out.println("📍 Intercepted: " + className + "#" + methodName+", Thread:"+Thread.currentThread().getName());

        long startTime = System.currentTimeMillis();
        String threadName = Thread.currentThread().getName();

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attrs != null ? attrs.getRequest() : null;

        if (request != null) {
            String url = request.getRequestURL().toString();
            String qs = request.getQueryString();
            String fullUrl = url + (qs != null ? "?" + qs : "");

//            String className = joinPoint.getSignature().getDeclaringTypeName();
//            String methodName = joinPoint.getSignature().getName();

            LOG.info("--------------------------------------------------");
            LOG.info("Thread: {}", threadName);
            LOG.info("Handler: {}#{}", className, methodName);
            LOG.info("Request URL: {}", fullUrl);

            if (url.contains("/pswd/") || url.contains("/text/")) {
                LOG.info("⚡ Matched endpoint: {}", url);
//                trackMethodCalls.set(true);
//                LOG.info("⚡ trackMethodCalls: {}", trackMethodCalls.get());
                MethodTrackingContext.enterController(); // 👈 mark start of controller logic
                LOG.info("⚡ trackMethodCalls: {}", MethodTrackingContext.isInController());
            }
        }

        Object result;
        try {
        	LOG.info(">>> [Controller] Before: {}", joinPoint.getSignature());
            result = joinPoint.proceed();
            LOG.info(">>> [Controller] After: {}", joinPoint.getSignature());
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            long mins = duration / 60000;
            long secs = (duration % 60000) / 1000;
            long ms = duration % 1000;

            LOG.info("Thread: {} Method: {} executed in {} min {} sec {} ms",
                     threadName, joinPoint.getSignature().getName(), mins, secs, ms);
            LOG.info("--------------------------------------------------");

//            trackMethodCalls.remove();
            MethodTrackingContext.exitController(); // 👈 clean up
        }
        return result;
    }

//    // Advice to wrap internal methods (service, dao, etc.), but exclude filters/infrastructure
//    @Around("execution(* com.github.yash777..*(..)) " +
//    
//			"&& !within(com.github.yash777.myworld.api..*) " +
////            "&& !within(com.vorwerk.dspro.aspect.LoggingAspect) " +
////    		"&& !within(com.vorwerk.dspro.aspect..*) " +
//////            "&& !within(com.vorwerk.dspro.aop..*) " +
////            "&& !within(com.vorwerk.dspro.plugin..*) " +
////            "&& !within(com.vorwerk.dspro.audit..*) " +
////            "&& !within(com.vorwerk.dspro.actor..*) " +
////            "&& !within(com.vorwerk.dspro.security..*) " +
//            
//            "&& !within(org.springframework.web.filter..*) " +
//            "&& !within(javax.servlet..*)")
//    public Object captureInternalMethodCalls(ProceedingJoinPoint joinPoint) throws Throwable {
//    	System.out.println("📍 Internal Method :"+Thread.currentThread().getName());
//        if (!trackMethodCalls.get()) {
//            return joinPoint.proceed();
//        }
//
//        String className = joinPoint.getSignature().getDeclaringTypeName();
//        String methodName = joinPoint.getSignature().getName();
//
//        long start = System.currentTimeMillis();
//        Object result = joinPoint.proceed();
//        long duration = System.currentTimeMillis() - start;
//
//        long mins = duration / 60000;
//        long secs = (duration % 60000) / 1000;
//
//        LOG.info("📍 Internal Method: {}#{} executed in {} min {} sec",
//                 className, methodName, mins, secs);
//
//        return result;
//    }
}
