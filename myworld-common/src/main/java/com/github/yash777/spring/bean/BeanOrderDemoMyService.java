package com.github.yash777.spring.bean;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BeanOrderDemoMyService {
    public BeanOrderDemoMyService() {
        log.info("3️⃣ @Service - stereotype.MyService instantiated");
        System.out.println("3️⃣ @Service - stereotype.MyService instantiated");
    }
}

