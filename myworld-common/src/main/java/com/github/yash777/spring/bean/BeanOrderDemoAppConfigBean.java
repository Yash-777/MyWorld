package com.github.yash777.spring.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class BeanOrderDemoAppConfigBean {

    public BeanOrderDemoAppConfigBean() {
        log.info("1️⃣ @Configuration - AppConfigBean constructor - Spring needs to register the beans defined inside them.");
        System.out.println("1️⃣ @Configuration - AppConfigBean constructor - Spring needs to register the beans defined inside them.");
    }
    
    @Bean
    public BeanOrderDemoSampleBean sampleBeanMarkHighPriority() {
    	// Force Early Execution of LogConfig - Make It a @Bean in a High-Priority Configuration
        log.info("2️⃣ @Bean - sampleBean registered"); // SampleBean a plain class, not a Spring-managed bean
        System.out.println("2️⃣ @Bean - sampleBean registered");
        return new BeanOrderDemoSampleBean();
    }
}
