package com.github.yash777.myworld.api;

import java.util.Properties;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main entry point for the WAR-based deployment of the Spring Boot application.
 * <p>
 * This class extends {@link SpringBootServletInitializer}, which allows the
 * application to be deployed as a traditional WAR file inside an external
 * servlet container like Tomcat, JBoss, or WebLogic.
 * </p>
 *
 * <p>
 * The application is conditionally enabled using {@link ConditionalOnProperty},
 * which checks the value of the property {@code server.app.war.deployment}.
 * When this property is set to {@code YES}, this class is active and initializes
 * a web application context. Otherwise, it is ignored.
 * </p>
 *
 * <p>
 * The {@link ComponentScan} annotation is configured to scan child modules or
 * packages starting with <code>com.github.yash777.myworld</code>.
 * </p>
 *
 * <p><b>IMPORTANT:</b> If {@code server.app.war.deployment=NO}, and this is the only
 * configuration class, Spring Boot will fail to start due to the missing embedded
 * web server configuration, leading to:
 * <pre>
 * org.springframework.boot.web.context.MissingWebServerFactoryBeanException
 * </pre>
 * </p>
 *
 * @author 🔐 Yash
 * 
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.github.yash777.myworld") // To scan configuration classes of child modules
@ConditionalOnProperty(
    value = "server.app.war.deployment", 
    havingValue = "YES", 
    matchIfMissing = false
)
public class MyApplicationWarDeployment extends SpringBootServletInitializer implements CommandLineRunner {

    /**
     * Entry point for the Spring Boot application.
     * This sets custom application properties and disables the Spring Boot banner.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MyApplicationWarDeployment.class);
        Properties props = new Properties();
        
        // Optional: Define any default properties here if needed.
        app.setBannerMode(Mode.CONSOLE); // Show banner in the console
        app.setDefaultProperties(props);
        
        app.run(args);
    }

    /**
     * This method is executed after the Spring application context is initialized.
     * Use it to perform any post-startup logic or initialization.
     *
     * @param args Command-line arguments.
     * @throws Exception if any error occurs during execution.
     */
    @Override
    public void run(String... args) throws Exception {
        // Post-startup logic can go here.
    }
}

/*
================================================================================
📝 Configuration Notes:

This class is intended for use in WAR deployment scenarios. It is only loaded 
when the property `server.app.war.deployment=YES` is explicitly set.

If this property is set to NO or omitted, and no alternative Spring Boot 
application class is available to bootstrap the context (like a CLI-based main 
class), the application will fail with:

❌ Exception:
org.springframework.boot.web.context.MissingWebServerFactoryBeanException:
No qualifying bean of type 'org.springframework.boot.web.servlet.server.ServletWebServerFactory' available

✅ Solution:
- Ensure you have a separate entry point for non-web contexts when needed.
- Or, provide a fallback configuration that disables web server initialization.
================================================================================
*/