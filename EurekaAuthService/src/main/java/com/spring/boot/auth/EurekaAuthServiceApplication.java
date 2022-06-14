package com.spring.boot.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class EurekaAuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaAuthServiceApplication.class, args);
	}	
}
