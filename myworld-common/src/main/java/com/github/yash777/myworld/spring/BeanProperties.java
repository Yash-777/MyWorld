package com.github.yash777.myworld.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;
import lombok.ToString;

@Configuration

@EnableConfigurationProperties(BeanProperties.class) // Make sure to enable configuration binding - In your main class or config
@PropertySource(ignoreResourceNotFound = true, value = {"classpath:application-log.properties"}) 
@ConfigurationProperties(prefix = "app.bean") // If not used IllegalStateException: No ConfigurationProperties annotation found on  'com.github.yash777.myworld.spring.BeanProperties'.

@Data @ToString
public class BeanProperties {
    private String test;
    public static final String TEST_BEAN = "app.bean.test.common";
}