package com.example.cosmocats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class CosmoCatsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CosmoCatsApplication.class, args);
    }

}