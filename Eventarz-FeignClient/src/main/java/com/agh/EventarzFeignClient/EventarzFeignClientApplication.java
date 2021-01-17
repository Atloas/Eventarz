package com.agh.EventarzFeignClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
@EnableFeignClients
@Controller
public class EventarzFeignClientApplication {

    @Autowired
    private GreetingClient greetingClient;

    @Value("${spring.application.name}")
    private String appName;
    @Value("${server.port}")
    private String port;

    public static void main(String[] args) {
        SpringApplication.run(EventarzFeignClientApplication.class, args);
    }

    @RequestMapping("/get-greeting")
    public String greeting(Model model) {
        model.addAttribute("greeting", greetingClient.greeting());
        model.addAttribute("feignMessage",
                String.format("Through %s at port %s.", appName, port));
        return "greeting-view";
    }
}
