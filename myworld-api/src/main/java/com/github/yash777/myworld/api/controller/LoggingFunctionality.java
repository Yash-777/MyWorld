package com.github.yash777.myworld.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.yash777.myworld.service.logging.CommonLoggingService;

import lombok.extern.slf4j.Slf4j;

@RestController(value = "slf4j-ComponentName")
@RequestMapping(value = "/slf4j")
@Slf4j
public class LoggingFunctionality {
	
    private final CommonLoggingService loggingService;
    // CI - Injection
    public LoggingFunctionality(CommonLoggingService loggingService) {
    	log.info("LoggingFunctionality uses CommonLoggingService Bean.");
        this.loggingService = loggingService;
    }

    @GetMapping("/test/log")
    public String testLogging() {
    	log.info("LoggingFunctionality testLogging.");
		System.out.println("LoggingFunctionality testLogging.");
        loggingService.logStartupMessage();
        loggingService.logUserAction("alice", "login");
        loggingService.logErrorExample();
        return "Logs triggered! ✅";
    }
}
