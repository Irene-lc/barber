package edu.upb.barber;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upb.barber.integracion.Sistema1AuthRequest;
import edu.upb.barber.integracion.Sistema1AuthResponse;
import edu.upb.barber.integracion.Sistema1CrearEmpresaRequest;
import edu.upb.barber.integracion.SistemaA;
import edu.upb.barber.repository.dto.request.EmpresaRequestDto;
import edu.upb.barber.repository.dto.response.EmpresaResponseDto;
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

	public static void main(String[] args) {
		SpringApplication.run(BarberApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Sistema1AuthRequest request = new Sistema1AuthRequest();
		request.setNombre("root");
		request.setPassword("Abc123**");
		Sistema1AuthResponse response = sistemaA.auth(request);
		System.out.println(response);
		String token = response.getAccessToken();

//		response = sistemaA.listarEmpresa(response.getAccessToken());
//		EmpresaRequestDto request1 = new EmpresaRequestDto();
//		request1.setNombre("prueba");
//		request1.setNit("1234");
//		request1.setTelefono("12345");
//		request1.setRazonSocial("123prueba");
//		EmpresaResponseDto responseDto = sistemaA.crearEmpresa(request1, token);

		List<EmpresaResponseDto> empresas = sistemaA.listarEmpresa(token);
		empresas.forEach(System.out::println);



	}

}
