package com.java.NBE4_5_3_7;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class Nbe4537Application {

	public static void main(String[] args) {
		SpringApplication.run(Nbe4537Application.class, args);
	}

}
