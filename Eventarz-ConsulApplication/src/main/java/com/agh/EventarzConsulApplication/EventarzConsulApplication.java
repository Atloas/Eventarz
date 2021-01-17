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

	private final WebClient.Builder loadBalancedWebClientBuilder;

	@Value("${server.port}")
	private String port;

	public EventarzConsulApplication(WebClient.Builder webClientBuilder) {
		this.loadBalancedWebClientBuilder = webClientBuilder;
	}

	public static void main(String[] args) {
		SpringApplication.run(EventarzConsulApplication.class, args);
	}

	@RequestMapping("/hi")
	public Mono<String> hi() {
		return loadBalancedWebClientBuilder.build().get().uri("http://127.0.0.1:8083/")
				.retrieve().bodyToMono(String.class)
				.map(greeting -> String.format("%s Through %s.", greeting, port));
	}

}
