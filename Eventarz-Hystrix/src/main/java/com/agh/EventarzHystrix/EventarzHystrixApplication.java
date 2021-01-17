package com.agh.EventarzHystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class EventarzHystrixApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventarzHystrixApplication.class, args);
    }

    @GetMapping("/")
    public String hi() {
        HystrixCommand.Setter config = HystrixCommand.Setter.withGroupKey(
                HystrixCommandGroupKey.Factory.asKey("HiServiceGroup"));

        HystrixCommandProperties.Setter commandProperties = HystrixCommandProperties.Setter();
        commandProperties.withExecutionTimeoutInMilliseconds(1000);
        commandProperties.withCircuitBreakerSleepWindowInMilliseconds(4000);
        commandProperties.withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD);
        commandProperties.withCircuitBreakerEnabled(true);
        commandProperties.withCircuitBreakerRequestVolumeThreshold(3);
        commandProperties.withFallbackEnabled(true);

        config.andCommandPropertiesDefaults(commandProperties);
        config.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                .withMaxQueueSize(10)
                .withCoreSize(3)
                .withQueueSizeRejectionThreshold(10));

        CommandHi commandHi = new CommandHi(config);
        return commandHi.execute();
    }

}