package com.agh.EventarzConsulApplication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@SpringBootApplication
@RestController
public class EventarzConsulApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventarzConsulApplication.class, args);
	}

	@RequestMapping("/hi")
	public Mono<String> hi() {
		return WebClient.builder().build().get().uri("http://consul-service.service.consul:8080/")
				.retrieve().bodyToMono(String.class)
				.map(greeting -> String.format("%s Through application2.", greeting));
	}

}
