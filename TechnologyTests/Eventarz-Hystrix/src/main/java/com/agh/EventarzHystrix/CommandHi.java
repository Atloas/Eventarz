package com.agh.EventarzHystrix;

import com.netflix.hystrix.HystrixCommand;
import org.springframework.web.reactive.function.client.WebClient;

public class CommandHi extends HystrixCommand<String> {

    CommandHi(Setter config) {
        super(config);
    }

    @Override
    protected String run() {
        return WebClient.builder().build().get().uri("http://localhost:8081/")
                .retrieve().bodyToMono(String.class).block();
    }

    @Override
    protected String getFallback() {
        return WebClient.builder().build().get().uri("http://localhost:8082/")
                .retrieve().bodyToMono(String.class).block();
    }
}
