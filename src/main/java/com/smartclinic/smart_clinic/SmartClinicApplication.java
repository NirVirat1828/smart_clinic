package com.smartclinic.smart_clinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.smartclinic")
@EntityScan(basePackages = "com.smartclinic.model")
public class SmartClinicApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartClinicApplication.class, args);
	}

}
