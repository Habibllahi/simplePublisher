package com.codetrik.simplePublisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.codetrik.BeanQualifier.FASTER_XML_MAPPER;

@Configuration
public class AppConfig {

    @Bean(FASTER_XML_MAPPER)
    public ObjectMapper mapper(){
        return new ObjectMapper();
    }
}
