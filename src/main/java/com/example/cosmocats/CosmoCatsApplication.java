package com.example.cosmocats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
        title = "Cosmo Cats Marketplace",
        description = "This is a simple API for Cosmo Cats Marketplace",
        version = "1.1",
        license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0.html")
    ),
    servers = {
    @Server(url = "http://localhost:8080", description = "test server"),
    }
)

@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
@EnableAspectJAutoProxy
public class CosmoCatsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CosmoCatsApplication.class, args);
    }

}