package com.github.yash777.myworld.service.logging;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * Common logging service to demonstrate usage of @Slf4j.
 * This can be used across modules to log messages with a shared logging setup.
 */
@Slf4j
@Service
public class CommonLoggingService  {

    public CommonLoggingService() {
        log.info("🚧 CommonLoggingService constructor: Before @Autowired and properties are set");
    }
	{
		log.info("CommonLoggingService - This runs immediately after constructor call, BEFORE dependencies are injected.");
		System.out.println("CommonLoggingService - This runs immediately after constructor call, BEFORE dependencies are injected.");
	}
    @PostConstruct
    public void init() {
        log.info("✅ CommonLoggingService Bean fully created and dependencies injected.");
        System.out.println("✅ CommonLoggingService Bean fully created and dependencies injected.");
    }
	
    public void logStartupMessage() {
        log.info("✅ CommonLoggingService initialized and ready to log!");
    }

    public void logUserAction(String username, String action) {
        log.debug("User [{}] performed action: {}", username, action);
    }

    public void logErrorExample() {
        try {
            throw new RuntimeException("Simulated exception for logging test.");
        } catch (Exception ex) {
            log.error("Something went wrong while processing!", ex);
        }
    }
}
