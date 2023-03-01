package com.example.demoprint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication
public class DemoprintApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoprintApplication.class, args);
    }

    public CommandLineRunner runner() {
        return args -> {

        };
    }
}
