package com.euandresimoes.spring_crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication()
@EnableJpaAuditing
public class SpringCrmApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCrmApplication.class, args);
	}

}
