package com.github.yash777.myworld.aspects.custom.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a method or class for execution time logging.
 * <p>
 * Use this annotation to log how long a method or all methods in a class take to execute.
 *
 * <h2>Usage Example</h2>
 *
 * <pre><code>
 * // Log execution time of all methods in a class
 * @LogExecutionTime
 * @Service
 * public class MyService {
 * 
 *     public void doSomething() {
 *         // method logic
 *     }
 * 
 *     public void doAnotherThing() {
 *         // method logic
 *     }
 * }
 *
 * // Log execution time of a single method
 * @Service
 * public class MyOtherService {
 * 
 *     @LogExecutionTime
 *     public void processTask() {
 *         // method logic
 *     }
 * }
 * </code></pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogExecutionTime {
}
