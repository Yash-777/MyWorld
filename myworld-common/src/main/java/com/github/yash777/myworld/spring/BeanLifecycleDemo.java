package com.github.yash777.myworld.spring;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.github.yash777.myworld.logback.slf4j.LogProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * Single Java class that demonstrates all 8 stages of the Spring Bean Lifecycle, complete with logs 
 * so you can watch the lifecycle unfold in order during app startup and shutdown.
 * 
 * @author yashwanth
 *
 */
@Configuration

//@EnableConfigurationProperties(BeanProperties.class)
@PropertySource(value = "classpath:application-log.properties", ignoreResourceNotFound = true)
@ConditionalOnProperty(value = BeanProperties.TEST_BEAN, havingValue = "YES", matchIfMissing = false)

@ComponentScan(basePackages = "com.github.yash777.spring.bean")
@Slf4j
public class BeanLifecycleDemo implements 
        BeanNameAware, BeanFactoryAware, ApplicationContextAware, 
        InitializingBean, DisposableBean, SmartInitializingSingleton {
	@Autowired
	private BeanProperties beanProperties;
	{
		// Why Not Use Instance Block?
		// This runs immediately after constructor call, BEFORE dependencies are injected.
	}
	
    public BeanLifecycleDemo() {
        log.info("1️⃣ Constructor called — Bean instance created");
    }
    @Override
    public void setBeanName(String name) {
        log.info("2️⃣ BeanNameAware — Bean name set: {}", name);
    }
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        log.info("3️⃣ BeanFactoryAware — BeanFactory injected");
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.info("4️⃣ ApplicationContextAware — ApplicationContext injected");
    }
    @PostConstruct
    public void postConstruct() {
        log.info("5️⃣ @PostConstruct — Bean fully constructed and dependencies injected :"+beanProperties);
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("6️⃣ InitializingBean — afterPropertiesSet() called");
    }
    @Override
    public void afterSingletonsInstantiated() {
        log.info("7️⃣ SmartInitializingSingleton — All singleton beans are now instantiated");
    }

    @PreDestroy
    public void preDestroy() {
        log.info("8️⃣ @PreDestroy — Pre-destroy method called before bean is destroyed");
    }
    @Override
    public void destroy() throws Exception {
        log.info("8️⃣ DisposableBean — destroy() method called during shutdown");
    }
}
