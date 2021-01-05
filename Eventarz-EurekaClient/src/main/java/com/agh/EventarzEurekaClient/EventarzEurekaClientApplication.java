package com.agh.EventarzEurekaClient;

import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class EventarzEurekaClientApplication implements GreetingController {

    @Autowired
    @Lazy
    private EurekaClient eurekaClient;

    @Value("${spring.application.name}")
    private String appName;
    @Value("${server.port}")
    private String port;

    public static void main(String[] args) {
        SpringApplication.run(EventarzEurekaClientApplication.class, args);
    }

    @Override
    public String greeting() {
        return String.format(
                "Hello from '%s' at port %s!", eurekaClient.getApplication(appName).getName(), port);
    }
}
