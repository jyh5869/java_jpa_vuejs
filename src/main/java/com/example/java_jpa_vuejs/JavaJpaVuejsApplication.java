package com.example.java_jpa_vuejs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class JavaJpaVuejsApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaJpaVuejsApplication.class, args);
	}

}
