package com.campsite.volcano;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.campsite")
public class VolcanoApplication {

	public static void main(String[] args) {
		SpringApplication.run(VolcanoApplication.class, args);
	}

}
