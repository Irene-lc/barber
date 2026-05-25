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


//@SpringBootApplication
//public class EventopApplication implements CommandLineRunner {
//	//CommandLineRunner: crear nueo hilo, despues de que se ejecuta springboot
//
//	//inyectar dependencia de eventosrepository
//	@Autowired
//	private EventosRepository eventosRepository;
//	@Autowired
//	private EmpresasRepository empresasRepository;
//
//	public static void main(String[] args) {
//		SpringApplication.run(EventopApplication.class, args);
//	}

//	@Override
//	public void run(String... args) throws Exception {
//		utilitario();
//	}
//	private void utilitario() {
//		if (this.empresasRepository.count() == 0) {
//			Empresas empresas = new Empresas();
//			empresas.setNombreEmpresa("Empresa 1");
//			empresas.setDescripcion("Empresa 1");
//			empresasRepository.save(empresas);
//		}
//		Empresas empresas = this.empresasRepository.findAll().get(0);
//
//		Eventos eventos = new Eventos();
//		eventos.setNombre("Eventos de prueba");
//		eventos.setDescripcion("Eventos de prueba descripcion");
//		eventos.setEmpresas(empresas);
//		eventosRepository.save(eventos);
//		for (Eventos eventos1 : eventosRepository.findAll()) {
//			eventos1.setNombre("Evento modificado");
//			eventosRepository.save(eventos1);
//		}
//	}

	//obtenga lista de eventos e impirma en pantalla
//	private void listEventos() {
//		System.out.println();
//		System.out.println("EVENTOS");
//		List<Eventos> eventos = eventosRepository.findAll();
//		for (Eventos e : eventos) {
//			System.out.println("Nombre: "+ e.getNombre() + ", Descipcion: "+ e.getDescripcion());
//			e.setNombre("Evento modificado");
//			eventosRepository.save(e);
//			System.out.println("Nombre: "+ e.getNombre() + ", Descipcion: "+ e.getDescripcion());
//		}
//	}
//}