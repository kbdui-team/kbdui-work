package org.example.intelligauge_back;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.example.intelligauge_back", "com.app", "client"})
@MapperScan("com.app")
public class IntelliGaugeBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntelliGaugeBackApplication.class, args);
	}

}
