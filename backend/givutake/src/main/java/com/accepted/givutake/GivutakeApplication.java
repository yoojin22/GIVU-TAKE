package com.accepted.givutake;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GivutakeApplication {

	public static void main(String[] args) {
		SpringApplication.run(GivutakeApplication.class, args);
	}

}
