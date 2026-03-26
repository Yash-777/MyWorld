package com.github.yash777.myworld.aspects;

import com.github.yash777.myworld.aspects.custom.annotation.LogExecutionTime;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aspect that logs the execution time of methods annotated with {@link LogExecutionTime}.
 * <p>
 * This aspect intercepts method calls and logs their execution duration at INFO level.
 *
 * <h2>How to Use</h2>
 * <ul>
 *   <li>Annotate a single method with {@code @LogExecutionTime} to log its execution time.</li>
 *   <li>Annotate an entire class with {@code @LogExecutionTime} to log execution time for all its methods.</li>
 * </ul>
 *
 * <h2>Example</h2>
 *
 * <pre><code>
 * // Logs all methods in this class
 * @LogExecutionTime
 * @Service
 * public class ExampleService {
 *
 *     public void processData() {
 *         // some processing logic
 *     }
 * }
 *
 * // Logs only the annotated method
 * @Service
 * public class AnotherService {
 *
 *     @LogExecutionTime
 *     public void runTask() {
 *         // some task logic
 *     }
 * }
 * </code></pre>
 *
 * @see LogExecutionTime
 */
//@Aspect
//@Component
public class MethodExecutionTimeAspect {

    private static final Logger logger = LoggerFactory.getLogger(MethodExecutionTimeAspect.class);

    /**
     * Logs execution time for methods annotated with {@link LogExecutionTime}.
     *
     * @param joinPoint the join point representing the intercepted method
     * @return the result of the method execution
     * @throws Throwable if the intercepted method throws any exception
     */
    @Around("@annotation(com.github.yash777.myworld.aspects.custom.annotation.LogExecutionTime) "   // method-level logging
          + " || within(@com.github.yash777.myworld.aspects.custom.annotation.LogExecutionTime *)") // class-level logging
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;
        logger.info("Thread: {} | Method [{}] executed in {} ms", Thread.currentThread().getName(), joinPoint.getSignature(), executionTime);

        return result;
    }
}
