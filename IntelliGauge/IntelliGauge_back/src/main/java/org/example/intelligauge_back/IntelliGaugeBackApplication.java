package org.example.intelligauge_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.example.intelligauge_back", "com.app.controller"})
public class IntelliGaugeBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntelliGaugeBackApplication.class, args);
	}

}
