package com.agh.EventarzEurekaServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class EventarzEurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventarzEurekaServerApplication.class, args);
	}

}
