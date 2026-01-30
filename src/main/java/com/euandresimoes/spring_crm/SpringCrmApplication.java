package com.euandresimoes.spring_crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class SpringCrmApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCrmApplication.class, args);
	}

}
