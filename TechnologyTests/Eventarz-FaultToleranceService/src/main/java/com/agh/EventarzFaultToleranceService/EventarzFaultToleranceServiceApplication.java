package com.agh.EventarzFaultToleranceService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class EventarzFaultToleranceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventarzFaultToleranceServiceApplication.class, args);
	}

	@GetMapping("/")
	public String home() {
		return "Hi from service!";
	}

}
