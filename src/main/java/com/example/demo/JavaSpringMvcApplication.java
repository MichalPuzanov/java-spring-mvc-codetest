package com.example.demo;

import com.example.demo.service.JsonFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.CompletableFuture;

@SpringBootApplication
@EnableAsync
@Slf4j
public class JavaSpringMvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaSpringMvcApplication.class, args);
    }

    @Autowired
    private JsonFileService jsonFileService;

    @Bean
    @Profile("upload")
    CommandLineRunner runner(JsonFileService jsonFileService) {
        return args -> {
            CompletableFuture<Integer> records = jsonFileService.uploadCustomersJson();
            log.info("Number of imported records: " + records.get());
        };
    }
}

