package com.github.yash777.myworld.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Aspect @Order(2)
//@Component
public class ApiEndpointPointcutLoggingAspect {
	private static final Logger logger = LoggerFactory.getLogger(ApiEndpointPointcutLoggingAspect.class);
	
	@Value("${performance.logging.enabled:true}")
	private boolean enabled;
	
	@Around("execution(* com.vorwerk.dspro.*..impl..*.*(..))")
	public Object logExecutionTime(final ProceedingJoinPoint joinPoint) throws Throwable {
//		if (!EndpointTimeAspect.reqTriggered.get()) {
//			return joinPoint.proceed(); // skip logic if not enabled
//		}
		
		if (!enabled) {
			return joinPoint.proceed();
		}
		
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		String className = methodSignature.getDeclaringType().getSimpleName();
		String methodName = methodSignature.getName();
		
		logger.info("Start Execution of {}.{}", className, methodName);
		
		long start = System.currentTimeMillis();
		Object result = joinPoint.proceed();
		long elapsed = System.currentTimeMillis() - start;
		
		logger.info("End Execution of {}.{} :: {} ms", className, methodName, elapsed);
		return result;
	}
}
