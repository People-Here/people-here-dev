package com.peoplehere.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication(scanBasePackages = {"com.peoplehere.api", "com.peoplehere.shared"})
public class ApiApplication {

	public static void main(String[] args) {
		var app = new SpringApplication(ApiApplication.class);
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);

	}
}
