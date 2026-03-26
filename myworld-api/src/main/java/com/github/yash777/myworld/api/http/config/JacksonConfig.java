package com.github.yash777.myworld.api.http.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class JacksonConfig {

//    @Bean
//    public ObjectMapper objectMapper() {
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
//        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, false); // don't sort alphabetically
//        return mapper;
//    }
    
//    @Bean
//    public ObjectMapper objectMapper() {
//        final ObjectMapper mapper = new ObjectMapper();
//        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        //mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
//        //mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, false); // don't sort alphabetically
//        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
//        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        
//        // since 2.10 recommended to use ALLOW_JAVA_COMMENTS - Java/C++ style comments (both '/'+'*' and '//' varieties)
//        mapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS, true);
//        //objectMapper.configure(com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_JAVA_COMMENTS, true);
//        return mapper;
//    }
}
