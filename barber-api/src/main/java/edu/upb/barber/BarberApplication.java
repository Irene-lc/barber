package edu.upb.barber;

import edu.upb.barber.repository.dto.request.StereumDto;
import edu.upb.barber.repository.dto.response.*;
import edu.upb.barber.service.integracion.SistemaA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

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

//		StereumDto stereumDto = new StereumDto();
//		stereumDto.setCountry("BO");
//		stereumDto.setAmount("100");
//		stereumDto.setCurrency("USDT");
//		stereumDto.setNetwork("POLYGON");
//		stereumDto.setIdempotencyKey("f3681c68-014b-48bf-b376-6445e68ed3db");
//		stereumDto.setChargeReason("Compra de prueba");
//		stereumDto.setReservationValidityTime("10");
//		stereumDto.setCustomer(new Customer("Ricardo", "Laredo", "76887344"));
//		StereumDto stereum =
//				sistemaA.consumirStereum("9cb7ac4d-4bca-4400-beb7-a84e0b3ad582", stereumDto);
//
//		System.out.println(stereum);
//		System.out.println("CLIENTE CREADO:");

		// LOGIN
//		Sistema1AuthRequest authRequest = new Sistema1AuthRequest();
//		authRequest.setNombre("Barber");
//		authRequest.setPassword("Abc123**");
//
//		Sistema1AuthResponse authResponse = sistemaA.auth(authRequest);
//
//		String token = authResponse.getAccessToken();
//
//		System.out.println("TOKEN:");
//		System.out.println(token);
//
//
//		List<EmpresaResponseDto> empresas = sistemaA.listarEmpresa(token);
//
//		System.out.println("LISTA EMPRESAS:");
//		System.out.println(empresas);
//
//
//
//		EmpresaRequestDto empresaRequest = new EmpresaRequestDto();

//		empresaRequest.setNombre("Empresa Nueva");
//		empresaRequest.setRazonSocial("Empresa Nueva SRL");
//		empresaRequest.setNit("7894568");
//		empresaRequest.setTelefono("77777777");
//		empresaRequest.setEmail("empresa1@gmail.com");
//		empresaRequest.setActivo(true);
//
//
//		EmpresaResponseDto empresaCreada =
//				sistemaA.crearEmpresa(token, empresaRequest);
//
//		System.out.println("EMPRESA CREADA:");
//		System.out.println(empresaCreada);


//		EmpresaRequestDto empresaUpdate = new EmpresaRequestDto();
//		empresaUpdate.setNombre("Empresa Modificada");
//		empresaUpdate.setNit("999999");
//
//		EmpresaResponseDto empresaActualizada =
//				sistemaA.actualizarEmpresa(token, 1L, empresaUpdate);
//
//		System.out.println("EMPRESA ACTUALIZADA:");
//		System.out.println(empresaActualizada);



//		UsuarioRequestDto usuarioRequest = new UsuarioRequestDto();
//		usuarioRequest.setNombre("Juan");
//		usuarioRequest.setEmail("juan@gmail.com");
//		usuarioRequest.setPassword("1234567");
//		usuarioRequest.setActivo(true);
//		usuarioRequest.setRol(RolUsuario.ROLE_CLIENTE);
//
//		UsuarioResponseDto usuarioCreado =
//				sistemaA.crearUsuario(token, usuarioRequest);
//
//		System.out.println("USUARIO CREADO:");
//		System.out.println(usuarioCreado);


//		ClienteRequestDto clienteRequest = new ClienteRequestDto();
//		clienteRequest.setNombre("Carlos");
//		clienteRequest.setTelefono("77777777");
//		clienteRequest.setEmpresa("8463d2cf-c18d-4fb0-ad80-3b0a3a77953e");
//		ClienteResponseDto clienteCreado =
//				sistemaA.crearCliente(token, clienteRequest);
//
//		System.out.println("CLIENTE CREADO:");
//		System.out.println(clienteCreado);
	}
}