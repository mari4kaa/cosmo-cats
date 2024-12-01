package com.example.cosmocats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
@EnableAspectJAutoProxy
public class CosmoCatsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CosmoCatsApplication.class, args);
    }

}