package edu.upb.barber;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BarberApplication {

	public static void main(String[] args) {
		SpringApplication.run(BarberApplication.class, args);
	}

}
