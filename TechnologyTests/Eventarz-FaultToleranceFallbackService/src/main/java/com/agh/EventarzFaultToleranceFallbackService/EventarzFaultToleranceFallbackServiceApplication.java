package com.agh.EventarzFaultToleranceFallbackService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class EventarzFaultToleranceFallbackServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventarzFaultToleranceFallbackServiceApplication.class, args);
	}

	@GetMapping("/")
	public String fallback() {
		return "Hi from fallback!";
	}

}
