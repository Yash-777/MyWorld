package com.github.yash777.spring.bean;

import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class BeanOrderDemoMyController {
    public BeanOrderDemoMyController() {
        log.info("5️⃣ @RestController | @Controller - stereotype.MyController instantiated");
        System.out.println("5️⃣ @RestController | @Controller - stereotype.MyController instantiated");
    }
}
