package com.github.yash777.spring.bean;

import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class BeanOrderDemoMyRepository {
    public BeanOrderDemoMyRepository() {
        log.info("4️⃣ @Repository - stereotype.MyRepository instantiated");
        System.out.println("4️⃣ @Repository - stereotype.MyRepository instantiated");
    }
}
