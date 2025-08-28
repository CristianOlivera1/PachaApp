package com.example.pachaapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling // importante para ejecutar el codigo de cambiar de estado automaticamente
@SpringBootApplication
public class PachaAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(PachaAppApplication.class, args);
	}

}
