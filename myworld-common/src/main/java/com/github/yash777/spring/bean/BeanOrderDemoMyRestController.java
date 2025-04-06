package com.github.yash777.spring.bean;

import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class BeanOrderDemoMyRestController {
    public BeanOrderDemoMyRestController() {
        log.info("5️⃣ @RestController | @Controller - web.bind.annotation.MyController instantiated");
        System.out.println("5️⃣ @RestController | @Controller - web.bind.annotation.MyController instantiated");
    }
}
