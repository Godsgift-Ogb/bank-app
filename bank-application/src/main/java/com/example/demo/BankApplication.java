package com.example.demo;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "The Java Academy Bank App ",
				description = "BackEnd Rest Apis For Bank Application",
				version = "v1.0",
				contact = @Contact(
						name = "Ogbonna Godsgift",
						email = "godsgiftgee78@gmail.com",
						url = "https://github.com/godsgift/bank_app"
				),
				license = @License(
						name = "The Bank App",
						url = "https://github.com/godsgift/bank_app"
				)
		),
		externalDocs = @ExternalDocumentation(
				description = " The Bank App Documentation",
				url = "https://github.com/godsgift/bank_app"

		)
)
public class BankApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankApplication.class, args);
	}

}
