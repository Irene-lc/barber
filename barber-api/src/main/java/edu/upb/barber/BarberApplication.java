package edu.upb.barber;

import edu.upb.barber.repository.dto.request.EmpresaRequestDto;
import edu.upb.barber.repository.dto.response.EmpresaResponseDto;
import edu.upb.barber.service.integracion.Sistema1AuthRequest;
import edu.upb.barber.service.integracion.Sistema1AuthResponse;
import edu.upb.barber.service.integracion.SistemaA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import java.util.List;

@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)

@EnableJpaAuditing
@SpringBootApplication

public class BarberApplication implements CommandLineRunner {
	@Autowired
	private SistemaA sistemaA;

	public static void main(String[] args) throws Exception {

		SpringApplication.run(BarberApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		Sistema1AuthRequest request = new Sistema1AuthRequest();
		request.setNombre("Barber");
		request.setPassword("Abc123**");
		Sistema1AuthResponse response = sistemaA.auth(request);
		System.out.println(response);

		List<EmpresaResponseDto> listar = sistemaA.listarEmpresa(response.getAccessToken());
		System.out.println(listar);
	}


}
