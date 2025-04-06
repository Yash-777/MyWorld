package com.github.yash777.myworld.logback.slf4j;

import javax.annotation.PostConstruct;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import ch.qos.logback.core.util.FileSize;
import lombok.extern.slf4j.Slf4j;

/**
 * When using Lombok’s {@code @Slf4j} and you want to configure logging with a RollingFileAppender in a common module, the logging is typically 
 * handled via Logback, since Spring Boot uses Logback as the default logging framework.
 * 
 * To set this up, you'd do the following:
 * 
 * <p><b>Configure RollingFileAppender in a Common Module:</b> Since {@code @Slf4j} is just a Lombok annotation that gives you access to a logger,
 *  the actual logging behavior (file logging, rolling, etc.) is handled by Logback’s configuration — either in logback.xml or logback-spring.xml.
 * 
 * <ul><li>RollingFileAppender: Writes logs to a file and rolls over when it reaches a size limit.
 * <li>SizeBasedRollingPolicy:
 * <ul><li>maxFileSize: Max file size before rollover.
 * <li>fileNamePattern: How the rolled files are named and compressed.
 * <li>maxHistory: How many backup files to keep.
 * </ul></ul>
 * 
 * <p>Create `logback-spring.xml` in the <code>resources/</code> of the common module: sample code in file `logback-spring_temp.xml`</p>
 * </p>
 * 
 * Workaround via Java Config Bean (Programmatic Configuration)
 * 
 * 
 * @author yashwanth
 *
 */
@Configuration
@Slf4j
public class LogConfig {
	public LogConfig() {
		log.info("🚧 LogConfig constructor: Before @Autowired and properties are set");
	}
	
	@Autowired
	private LogProperties logProperties;
	
	@PostConstruct
	public void setupLogger() {
		log.info("PostConstruct 🔧 Initializing logger setup with properties: {}", logProperties);
		log.info("📂 Loaded Logging Properties from log-config/application-log.properties: {}", logProperties);
		System.out.println("PostConstruct 🔧 LogProperties: " + logProperties);
		
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		
		// Setup the encoder
		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		encoder.setContext(context);
		encoder.setPattern(logProperties.getPattern());
		encoder.start();
		
		// Setup the rolling file appender
		RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<>();
		fileAppender.setContext(context);
		fileAppender.setFile(logProperties.getFilePath() + "/" + logProperties.getFileName());
		
		fileAppender.setAppend(true); // ✅ Ensure it appends to the existing log file
		
		fileAppender.setEncoder(encoder);
		
		// Determine extension
		String extension = logProperties.getFileExtension().toLowerCase().startsWith(".")
				? logProperties.getFileExtension()
						: "." + logProperties.getFileExtension();
		
		// Rolling policy
		FixedWindowRollingPolicy rollingPolicy = new FixedWindowRollingPolicy();
		rollingPolicy.setContext(context);
		rollingPolicy.setParent(fileAppender);
		rollingPolicy.setFileNamePattern(logProperties.getFilePath() + "/" + logProperties.getFileName() + ".%i" + extension); // .gz or .zip
		rollingPolicy.setMinIndex(1);
		rollingPolicy.setMaxIndex(logProperties.getMaxBackups());
		rollingPolicy.start();
		
		// Triggering policy
		SizeBasedTriggeringPolicy<ILoggingEvent> triggeringPolicy = new SizeBasedTriggeringPolicy<>();
		triggeringPolicy.setContext(context);
		triggeringPolicy.setMaxFileSize(new FileSize(logProperties.getMaxSizeMb() * 1024 * 1024)); // Convert MB to bytes
		triggeringPolicy.start();
		
		fileAppender.setRollingPolicy(rollingPolicy);
		fileAppender.setTriggeringPolicy(triggeringPolicy);
		fileAppender.start();
		
		// Register this appender with root logger
		Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
		rootLogger.addAppender(fileAppender);
	}
}