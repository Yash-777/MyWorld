package com.github.yash777.myworld.aspects2;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Internal - Class need bee spring loaded and call need to happen with spring bean, not custom object
@Autowired Internal obj;

>>> [Controller] Before: MyController.myEndpoint
>>> [Internal] Before: MyService.myMethod
<<< [Internal] After: MyService.myMethod
<<< [Controller] After: MyController.myEndpoint
 * if custom obj aspect witll not call
Internal obj = new Internal();
>>> [Controller] Before: MyController.myEndpoint
<<< [Controller] After: MyController.myEndpoint

 * @author ymerugu
 *
 */
@Component @Aspect @org.springframework.core.annotation.Order(2)
public class ControllerLoggingInternalMethodAspect {
	private static final Logger LOG = LoggerFactory.getLogger(ControllerLoggingInternalMethodAspect.class);
	
	// Advice to wrap internal methods (service, dao, etc.), but exclude filters/infrastructure
    @Around("execution(* com.github.yash777..*(..)) " +
//    		@Around("execution(* com.github.yash777..*.*(..)) " +
    //🔍 Intercepted: org.springframework.boot.web.server.WebServerFactoryCustomizer#customize, Thread:restartedMain
			"&& !within(org.springframework.boot..*) " +
//	🔍 Intercepted: com.github.yash777.myworld.logback.slf4j.LogProperties#toString, Thread:restartedMain
//	🔍 Intercepted: com.github.yash777.myworld.security.SecurityConfig_HttpBasic_FormLogin#sessionTimeoutCustomizer, Thread:restartedMain
			"&& !within(com.github.yash777.myworld.logback..*) " +
			"&& !within(com.github.yash777.myworld.security..*) " +
			
//	🔍 Intercepted: com.github.yash777.myworld.aspects.monitor.CachedBodyFilter#doFilter, Thread:http-nio-8080-exec-1
"&& !within(com.github.yash777.myworld.aspects..*) " +
			
			"&& !within(com.github.yash777.myworld.api..*) " +
            "&& !within(org.springframework.web.filter..*) " +
            "&& !within(javax.servlet..*)")
    public Object captureInternalMethodCalls(ProceedingJoinPoint joinPoint) throws Throwable {
    	String className = joinPoint.getSignature().getDeclaringTypeName();
    	String methodName = joinPoint.getSignature().getName();
    	System.out.println("🔍 Intercepted: " + className + "#" + methodName+", Thread:"+Thread.currentThread().getName());
    	
    	LOG.info("⚡ trackMethodCalls: {}", MethodTrackingContext.isInController());
//    	LOG.info("⚡ trackMethodCalls: {}", ControllerLoggingAspect.trackMethodCalls.get());
//        if (!ControllerLoggingAspect.trackMethodCalls.get()) {
//            return joinPoint.proceed();
//        }
    	// Only log if we're inside a controller flow
    	if (!MethodTrackingContext.isInController()) {
            // Don't log if not part of a controller-request call
            return joinPoint.proceed();
        }

//        String className = joinPoint.getSignature().getDeclaringTypeName();
//        String methodName = joinPoint.getSignature().getName();

        long start = System.currentTimeMillis();
        LOG.info(">>> [Internal] Before: {}", joinPoint.getSignature());
        Object result = joinPoint.proceed();
        LOG.info(">>> [Internal] After: {}", joinPoint.getSignature());
        long duration = System.currentTimeMillis() - start;

        long mins = duration / 60000;
        long secs = (duration % 60000) / 1000;

        LOG.info("📍 Internal Method: {}#{} executed in {} min {} sec",
                 className, methodName, mins, secs);

        return result;
    }
}
