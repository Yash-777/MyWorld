package com.github.yash777.myworld.logback.slf4j;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;
import lombok.ToString;

@Configuration

@EnableConfigurationProperties(LogProperties.class) // Make sure to enable configuration binding - In your main class or config
// Location: myapp-common/src/main/resources/log-config.properties or application-log.properties
// Trying to load a module-specific application-log.properties file and bind it with @ConfigurationProperties
@PropertySource(ignoreResourceNotFound = true, value = {"classpath:log-config.properties", "classpath:application-log.properties"}) 
@ConfigurationProperties(prefix = "log")
// Use hyphen-case in properties if you're using @ConfigurationProperties, unless you bind with relaxed naming. Spring Boot will bind log.file-path to filePath.
@Data @ToString
public class LogProperties {
    private String filePath, fileName, pattern;
    private long maxSizeMb;
    private int maxBackups;
    private String fileExtension = "gz"; // default to gz if not set
}