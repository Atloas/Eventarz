package com.agh.eventarzPortal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class. Simply fires the Spring run() method.
 */
@SpringBootApplication
public class EventarzPortalApplication {

    private final static Logger log = LoggerFactory.getLogger(EventarzPortalApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(EventarzPortalApplication.class, args);
    }
}
