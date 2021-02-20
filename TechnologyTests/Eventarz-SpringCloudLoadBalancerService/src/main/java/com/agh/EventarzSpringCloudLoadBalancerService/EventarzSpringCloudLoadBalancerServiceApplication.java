package com.agh.EventarzSpringCloudLoadBalancerService;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class EventarzSpringCloudLoadBalancerServiceApplication {

	private static Logger log = LoggerFactory.getLogger(EventarzSpringCloudLoadBalancerServiceApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(EventarzSpringCloudLoadBalancerServiceApplication.class, args);
	}

	@Value("${server.port}")
	private String port;

	@GetMapping("/")
	public String home() {
		log.info("Access /");
		return "Hi from " + port;
	}
}
