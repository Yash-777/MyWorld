package com.github.yash777.spring.bean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BeanOrderDemoSampleBean {
    public BeanOrderDemoSampleBean() {
        log.info("🔸 SampleBean - @Bean instance created");
    }
}
