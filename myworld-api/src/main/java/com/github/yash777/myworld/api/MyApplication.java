package com.github.yash777.myworld.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main entry point for a standalone or embedded web server deployment of the Spring Boot application.
 * <p>
 * This class is conditionally loaded only when the property
 * <strong>{@code server.app.war.deployment=NO}</strong> is set. It assumes that the application is
 * running in a mode that requires an embedded web server (like Tomcat), and not being deployed as a WAR file
 * inside a traditional servlet container.
 * </p>
 *
 * <p>
 * The {@code @SpringBootApplication} annotation combines:
 * <ul>
 *   <li>{@code @Configuration}</li>
 *   <li>{@code @EnableAutoConfiguration}</li>
 *   <li>{@code @ComponentScan}</li>
 * </ul>
 * </p>
 *
 * <p>
 * The {@link ComponentScan} is set to scan the package <code>com.github.yash777.myworld</code>,
 * allowing modular and child-project configurations to be auto-detected.
 * </p>
 *
 * <p>
 * When this class is enabled, Spring Boot attempts to start an embedded servlet container
 * (like Tomcat or Jetty). If the property <code>server.app.war.deployment=NO</code> is missing
 * or another conflicting configuration is active, the application may fail with:
 * </p>
 *
 * <pre>
 * org.springframework.boot.web.context.MissingWebServerFactoryBeanException:
 * No qualifying bean of type 'org.springframework.boot.web.servlet.server.ServletWebServerFactory' available
 * </pre>
 *
 * <p><strong>Purpose:</strong> This class supports standalone (non-WAR) deployment using an embedded web server.</p>
 *
 * @author 🔐 Yash
 * 
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.github.yash777.myworld") // Scans configuration classes of child modules
@ConditionalOnProperty(
    value = "server.app.war.deployment", 
    havingValue = "NO", 
    matchIfMissing = false
)
public class MyApplication {

    /**
     * Standard Spring Boot entry point for launching the application with an embedded web server.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}

/*
================================================================================
📝 Configuration Notes:

✔ Use this class when:
- You want to run the application as a standalone JAR with an embedded web server.
- You are NOT deploying it as a WAR file in an external servlet container.

🔧 Enable this class by setting the property:
    server.app.war.deployment=NO

❌ If this property is not set, and no other Spring Boot Application class is active,
   Spring Boot may fail with:

   org.springframework.boot.web.context.MissingWebServerFactoryBeanException:
   No qualifying bean of type 'ServletWebServerFactory' available

✅ Recommended Setup:
- Define `server.app.war.deployment=NO` in application.properties or pass as a runtime argument.
- Use in combination with `MyApplicationWarDeployment` class to switch between WAR and JAR modes.

================================================================================
*/