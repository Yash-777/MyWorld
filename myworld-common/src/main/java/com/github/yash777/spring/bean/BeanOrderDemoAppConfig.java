package com.github.yash777.spring.bean;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class BeanOrderDemoAppConfig implements InitializingBean, SmartInitializingSingleton, ApplicationListener<ApplicationPreparedEvent> {

	{
		log.info("AppConfig - This runs immediately after constructor call, BEFORE dependencies are injected.");
		System.out.println("AppConfig - This runs immediately after constructor call, BEFORE dependencies are injected.");
	}
	
	public BeanOrderDemoAppConfig() {
		log.info("1️⃣ @Configuration - AppConfig constructor - with out Bean register");
		log.info("🚧 AppConfig constructor: Before @Autowired and properties are set");
		System.out.println("1️⃣ @Configuration - AppConfig constructor - with out Bean register");
	}
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("InitializingBean ✅ AppConfig initialized via afterPropertiesSet()");
		System.out.println("InitializingBean ✅ AppConfig initialized via afterPropertiesSet()");
	}
	
	// Use SmartInitializingSingleton (Advanced): If you want to run your logic after all singletons are created, but before the app starts, implement SmartInitializingSingleton:
	@Override // - implements SmartInitializingSingleton
	public void afterSingletonsInstantiated() {
		log.info("SmartInitializingSingleton 🔧 afterSingletonsInstantiated()");
	}
	
	@PostConstruct
	public void init() {
		log.info("PostConstruct 🔧 ✅ AppConfig Bean fully created and dependencies injected.");
		System.out.println("PostConstruct 🔧 ✅ AppConfig Bean fully created and dependencies injected.");
	}
	
	@Override // implements ApplicationListener<ApplicationPreparedEvent>
	public void onApplicationEvent(ApplicationPreparedEvent event) {
		//If you want the log setup to happen even before any beans are created, you can register an event listener:
		// This gives you access to the Environment and lets you initialize logging even earlier.
		log.info("ApplicationListener<ApplicationPreparedEvent> 🔧 onApplicationEvent()");
	}
}
