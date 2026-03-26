package com.github.yash777.myworld.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

//@Aspect
//@Component
public class RepositoryUsageAspect {
	private static final Logger logger = LoggerFactory.getLogger(RepositoryUsageAspect.class);
	
	// The pointcut expression must be a compile-time constant expression (i.e., a hardcoded string)
	static final String pointcutExpression = "execution(* com.vorwerk.dspro.configuration.repository.CompanyAuthenticationRepository.*(..))";
	@Around(pointcutExpression)
		
	//@Around("execution(* com.vorwerk.dspro.configuration.repository.CompanyAuthenticationRepository.*(..))")
	public Object logRepositoryCallAndCaller(ProceedingJoinPoint pjp) throws Throwable {
		long startTime = System.currentTimeMillis();
		
		// Repository method being invoked
		Signature signature = pjp.getSignature();
		String repoMethod = signature.getDeclaringTypeName() + "." + signature.getName();
		
		// Extract caller method from stack trace
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		String callerInfo = "Unknown";
		for (int i = 0; i < stackTrace.length; i++) {
			StackTraceElement element = stackTrace[i];
			// Skip framework/internal calls and stop at first external caller
			if (element.getClassName().startsWith("com.vorwerk.dspro.") && !element.getClassName().contains("RepositoryUsageAspect")) {
				callerInfo = element.getClassName() + "." + element.getMethodName() +
						" (Line: " + element.getLineNumber() + ")";
				break;
			}
		}
		
		logger.info("Thread: {} | Repo Method: {} | Called by: {} | Start Time: {} ms",
				Thread.currentThread().getName(),
				repoMethod,
				callerInfo,
				startTime);
		
		Object result = pjp.proceed();
		
		long endTime = System.currentTimeMillis();
		logger.info("Thread: {} | Repo Method: {} | Called by: {} | End Time: {} ms | Duration: {} ms",
				Thread.currentThread().getName(),
				repoMethod,
				callerInfo,
				endTime,
				(endTime - startTime));
		
		return result;
	}
	
}

/*
//@Around("(execution(* org.springframework.data.repository.Repository+.*(..)) || " +
//"execution(* com.vorwerk.dspro.usermanagement..*Repository.*(..))) && " +
//"this(com.vorwerk.dspro.usermanagement.repository.UserRepository)")
 * 
//@Around("execution(* com.vorwerk.dspro.configuration.repository.CompanyAuthenticationRepository.*(..))")
	public Object logRepositoryCall(ProceedingJoinPoint pjp) throws Throwable {
		long startTime = System.currentTimeMillis();
		logger.info("Thread: {} | {}.{} - Start Time: {} ms",
				Thread.currentThread().getName(),
				this.getClass().getSimpleName(),
				new Object() {}.getClass().getEnclosingMethod().getName(),
				startTime);
		
		// Proceed with the actual repository method call
		Object result = pjp.proceed();
		
		long endTime = System.currentTimeMillis();
		logger.info("Thread: {} | {}.{} - End Time: {} ms, Total Time Taken: {} ms",
				Thread.currentThread().getName(),
				this.getClass().getSimpleName(),
				new Object() {}.getClass().getEnclosingMethod().getName(),
				endTime,
				(endTime - startTime));
		return result;
	}
 */
