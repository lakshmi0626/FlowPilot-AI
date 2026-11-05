package com.flowpilot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FlowPilotApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlowPilotApplication.class, args);
    }
}
