package com.agh.EventarzResilience4j;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.ConnectException;

@RestController
@SpringBootApplication
public class EventarzResilience4jApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventarzResilience4jApplication.class, args);
    }

    @GetMapping("/")
    @Retry(name = "Hi Retry", fallbackMethod = "hiFallback")
    @Bulkhead(name = "Hi Bulkhead", fallbackMethod = "hiFallback")
    @CircuitBreaker(name = "Hi CircuitBreaker", fallbackMethod = "hiFallback")
    public Mono<String> hi() {
        return WebClient.builder().build().get().uri("http://localhost:8081/")
                .retrieve().bodyToMono(String.class);
    }

    public Mono<String> hiFallback(ConnectException e) {
        return WebClient.builder().build().get().uri("http://localhost:8082/")
                .retrieve().bodyToMono(String.class);
    }

}
