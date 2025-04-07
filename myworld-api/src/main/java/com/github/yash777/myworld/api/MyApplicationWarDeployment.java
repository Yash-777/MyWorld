package com.github.yash777.myworld.api;

import java.util.Properties;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.github.yash777.myworld") // To scan configuration classes of Child-Modules

@ConditionalOnProperty(value = "server.app.war.deployment", havingValue = "YES", matchIfMissing = false)
public class MyApplicationWarDeployment extends SpringBootServletInitializer implements CommandLineRunner {

	public static void main(String[] args) {
		//SpringApplication.run(MyApplicationWarDeployment.class, args);
		SpringApplication app = new SpringApplication(MyApplicationWarDeployment.class);
		Properties props = new Properties();
		app.setBannerMode(Mode.CONSOLE);
		app.setDefaultProperties(props);
		
		app.run();
	}

	@Override
	public void run(String... args) throws Exception {
		
	}

}