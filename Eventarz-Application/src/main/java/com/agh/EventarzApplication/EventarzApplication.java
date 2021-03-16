package com.agh.EventarzApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class EventarzApplication {

    private final static Logger log = LoggerFactory.getLogger(EventarzApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(EventarzApplication.class, args);
    }
}
