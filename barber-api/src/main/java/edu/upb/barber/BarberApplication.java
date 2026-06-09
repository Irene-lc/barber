package edu.upb.barber;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@EnableJpaAuditing
@SpringBootApplication

public class BarberApplication{

	public static void main(String[] args) {
		SpringApplication.run(BarberApplication.class, args);
	}
}
