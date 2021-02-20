package com.agh.EventarzSpringCloudLoadBalancer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@SpringBootApplication
public class EventarzSpringCloudLoadBalancerApplication {

    private final WebClient.Builder loadBalancedWebClientBuilder;

    public EventarzSpringCloudLoadBalancerApplication(WebClient.Builder webClientBuilder) {
        this.loadBalancedWebClientBuilder = webClientBuilder;
    }

    public static void main(String[] args) {
        SpringApplication.run(EventarzSpringCloudLoadBalancerApplication.class, args);
    }

    @RequestMapping("/hi")
    public Mono<String> hi() {
        return loadBalancedWebClientBuilder.build().get().uri("http://say-hello/")
                .retrieve().bodyToMono(String.class)
                .map(greeting -> String.format("%s", greeting));
    }

}
