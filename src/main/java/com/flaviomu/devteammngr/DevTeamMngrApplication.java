package com.flaviomu.devteammngr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;


/**
 * Defines the entry point for the DEV TEAM MANAGER Application
 *
 */
@Configuration
@SpringBootApplication
public class DevTeamMngrApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevTeamMngrApplication.class, args);
	}
}
