package com.github.yash777.myworld.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Coordinating Multiple Ordered Aspects in Spring with Shared Context.
 * 
 * <pre>
HTTP Request Thread:
 └──> RequestLoggingAspect (Order 1)
       └──> Sets enabled = true
       └──> Proceeds
             └──> AroundMethodAspect (Order 2)
                   └──> Checks enabled
                   └──> Executes conditional logic
             └──> Controller Method
       └──> Finally block: enabled.remove()
 * </pre>
 * 
<ul>✅ Key Notes
<li>ThreadLocal makes it thread-safe and request-isolated — each incoming request thread gets its own copy of the enabled flag.
<li>Static ThreadLocal is safe if used correctly (with .remove()).
<li>@Order is essential to enforce execution precedence of aspects.
</ul>
 *
 * @author Yash
 *
 */
@Aspect @Order(2) // Executes after EndpointTimeAspect
@Component
public class AroundMethodAspect {
	private static final Logger logger = LoggerFactory.getLogger(AroundMethodAspect.class);
	
	@Value("${aroundMethod.logging.enabled:true}")
	private boolean enabled;
	
	@Around("execution(* com.github.yash777.myworld.*..impl..*.*(..))")
	public Object logConditionalLogic(final ProceedingJoinPoint joinPoint) throws Throwable {
		if (!RequestLoggingAspect.reqTriggered.get()) {
			// Skip this logic if not enabled by the previous aspect
			return joinPoint.proceed();
		}
		
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
