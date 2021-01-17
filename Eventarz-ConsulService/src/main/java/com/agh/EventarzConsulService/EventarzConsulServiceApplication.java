package com.agh.EventarzConsulService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class EventarzConsulServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventarzConsulServiceApplication.class, args);
    }

    @Value("${server.port}")
    private String port;

    @GetMapping("/")
    public String home() {
        return "Hi from " + port + "!";
    }
}
