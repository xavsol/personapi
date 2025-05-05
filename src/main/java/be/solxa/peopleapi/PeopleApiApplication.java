package be.solxa.peopleapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "People Management API",
                version = "1.0",
                description = "REST API for managing people in a database"
        )
)
public class PeopleApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(PeopleApiApplication.class,args);
    }
}