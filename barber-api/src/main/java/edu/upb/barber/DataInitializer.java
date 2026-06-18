package edu.upb.barber;

import edu.upb.barber.repository.*;
import edu.upb.barber.repository.entity.*;
import edu.upb.barber.repository.entity.enums.*;
import edu.upb.barber.service.EmailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Slf4j
@Component
@AllArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository    usuarioRepository;
    private final EmpresaRepository    empresaRepository;
    private final SucursalRepository   sucursalRepository;
    private final ClienteRepository    clienteRepository;
    private final EmpleadoRepository   empleadoRepository;
    private final EspecieRepository    especieRepository;
    private final RazaRepository       razaRepository;
    private final MascotaRepository    mascotaRepository;
    private final PasswordEncoder      passwordEncoder;
    private final ProductoRepository   productoRepository;
    private final ServicioRepository   servicioRepository;
    private final AgendaEventoRepository   agendaEventoRepository;
    private final AgendaEventoDetalleRepository   agendaEventoDetalleRepository;
    private final AgendaEventoEmpleadoRepository  agendaEventoEmpleadoRepository;
    private final EmpleadoSucursalRepository      empleadoSucursalRepository;
    private final EmailService emailService;

    @Override
    public void run(String... args) {
        init();
        emailService.sendPassword("irene.landivar13@gmail.com","Irene","prueba");
    }

    @Transactional
    public void init() {
        String password = "Abc123**";
        log.info("DataInitializer: verificando datos de prueba...");

        // ─────────────────────────────────────────────────────
        // USUARIO SUPER ADMIN (sin empresa — acceso global)
        // ─────────────────────────────────────────────────────
        if (usuarioRepository.findByEmail("root@upb.com").isEmpty()) {
            usuarioRepository.save(Usuario.builder()
                    .nombre("root")
                    .apellido("Admin")
                    .email("root@upb.com")
                    .rol(RolUsuario.ROLE_ADMIN_EMPRESA)
                    .passwordHash(passwordEncoder.encode(password))
                    .activo(true).build());
            log.info("Usuario root@upb.com creado");
        }

        // ─────────────────────────────────────────────────────
        // EMPRESA 1: BARBERÍA "Klipp Barber Studio"
        // ─────────────────────────────────────────────────────
        if (empresaRepository.findAll().stream()
                .noneMatch(e -> e.getNit().equals("1000000001"))) {

            Empresa barberia = new Empresa();
            barberia.setNombre("Klipp Barber Studio");
            barberia.setRazonSocial("Klipp Barberia SRL");
            barberia.setNit("1000000001");
            barberia.setTelefono("70100001");
            barberia.setEmail("admin@klipp-barber.com");
            barberia.setTipoEmpresa(TipoEmpresa.BARBERIA);
            barberia.setActivo(true);
            empresaRepository.save(barberia);

            // Sucursal de barbería
            Sucursal sucBarber = new Sucursal();
            sucBarber.setEmpresa(barberia);
            sucBarber.setNombre("Sucursal Equipetrol");
            sucBarber.setDireccion("Av. San Martín, Equipetrol Norte");
            sucBarber.setTelefono("70100002");
            sucBarber.setActivo(true);
            sucursalRepository.save(sucBarber);

            // Admin de barbería
            Usuario adminBarber = Usuario.builder()
                    .nombre("admin_barber")
                    .apellido("Ríos")
                    .email("admin@klipp-barber.com")
                    .empresa(barberia)
                    .rol(RolUsuario.ROLE_ADMIN_EMPRESA)
                    .passwordHash(passwordEncoder.encode(password))
                    .activo(true).build();
            usuarioRepository.save(adminBarber);

            // Recepcionista de barbería
            Usuario recepBarber = Usuario.builder()
                    .nombre("recep_barber")
                    .apellido("Gomez")
                    .email("recep@klipp-barber.com")
                    .empresa(barberia)
                    .rol(RolUsuario.ROLE_RECEPCIONISTA)
                    .passwordHash(passwordEncoder.encode(password))
                    .activo(true).build();
            usuarioRepository.save(recepBarber);

            // Empleados barbería
            Usuario uMiguel = Usuario.builder()
                    .nombre("miguel")
                    .apellido("Flores")
                    .email("miguel@klipp-barber.com")
                    .empresa(barberia)
                    .rol(RolUsuario.ROLE_EMPLEADO)
                    .passwordHash(passwordEncoder.encode(password))
                    .activo(true).build();
            usuarioRepository.save(uMiguel);

            Empleado eMiguel = new Empleado();
            eMiguel.setUsuario(uMiguel);
            eMiguel.setEmpresa(barberia);
            eMiguel.setNombre("Miguel Flores");
            eMiguel.setTelefono("70100010");
            eMiguel.setCargo(CargoEmpleado.BARBERO);
            eMiguel.setEspecialidad("Cortes clásicos · Skin fade · Afeitado");
            eMiguel.setFotoUrl("MF");
            eMiguel.setDisponible(true);
            eMiguel.setActivo(true);
            empleadoRepository.save(eMiguel);

            EmpleadoSucursal esMiguel = new EmpleadoSucursal();
            esMiguel.setEmpleado(eMiguel);
            esMiguel.setSucursal(sucBarber);
            esMiguel.setActivo(true);
            empleadoSucursalRepository.save(esMiguel);

            Usuario uCarlos = Usuario.builder()
                    .nombre("carlos")
                    .apellido("Mendoza")
                    .email("carlos@klipp-barber.com")
                    .empresa(barberia)
                    .rol(RolUsuario.ROLE_EMPLEADO)
                    .passwordHash(passwordEncoder.encode(password))
                    .activo(true).build();
            usuarioRepository.save(uCarlos);

            Empleado eCarlos = new Empleado();
            eCarlos.setUsuario(uCarlos);
            eCarlos.setEmpresa(barberia);
            eCarlos.setNombre("Carlos Mendoza");
            eCarlos.setTelefono("70100011");
            eCarlos.setCargo(CargoEmpleado.BARBERO);
            eCarlos.setEspecialidad("Degradados · Diseño de barba · Color");
            eCarlos.setFotoUrl("CM");
            eCarlos.setDisponible(true);
            eCarlos.setActivo(true);
            empleadoRepository.save(eCarlos);

            EmpleadoSucursal esCarlos = new EmpleadoSucursal();
            esCarlos.setEmpleado(eCarlos);
            esCarlos.setSucursal(sucBarber);
            esCarlos.setActivo(true);
            empleadoSucursalRepository.save(esCarlos);

            // Clientes de barbería
            Cliente c1 = new Cliente();
            c1.setEmpresa(barberia);
            c1.setNombre("Juan Pérez");
            c1.setTelefono("70200001");
            c1.setEmail("juan@gmail.com");
            c1.setDocumento("10010020");
            c1.setActivo(true);
            clienteRepository.save(c1);

            Cliente c2 = new Cliente();
            c2.setEmpresa(barberia);
            c2.setNombre("Diego Soria");
            c2.setTelefono("70200002");
            c2.setEmail("diego@gmail.com");
            c2.setDocumento("10010021");
            c2.setActivo(true);
            clienteRepository.save(c2);

            log.info("Empresa BARBERIA 'Klipp Barber Studio' creada con sucursal, usuarios y clientes");
        }

        // ─────────────────────────────────────────────────────
        // EMPRESA 2: SALÓN "Klipp Salon Lab"
        // ─────────────────────────────────────────────────────
        if (empresaRepository.findAll().stream()
                .noneMatch(e -> e.getNit().equals("1000000002"))) {

            Empresa salon = new Empresa();
            salon.setNombre("Klipp Salon Lab");
            salon.setRazonSocial("Klipp Salon de Belleza SRL");
            salon.setNit("1000000002");
            salon.setTelefono("70200001");
            salon.setEmail("admin@klipp-salon.com");
            salon.setTipoEmpresa(TipoEmpresa.SALON);
            salon.setActivo(true);
            empresaRepository.save(salon);

            // Sucursal de salón
            Sucursal sucSalon = new Sucursal();
            sucSalon.setEmpresa(salon);
            sucSalon.setNombre("Sucursal Las Palmas");
            sucSalon.setDireccion("Av. Las Palmas, 3er anillo");
            sucSalon.setTelefono("70200002");
            sucSalon.setActivo(true);
            sucursalRepository.save(sucSalon);

            // Admin de salón
            Usuario adminSalon = Usuario.builder()
                    .nombre("admin_salon")
                    .apellido("Castillo")
                    .email("admin@klipp-salon.com")
                    .empresa(salon)
                    .rol(RolUsuario.ROLE_ADMIN_EMPRESA)
                    .passwordHash(passwordEncoder.encode(password))
                    .activo(true).build();
            usuarioRepository.save(adminSalon);

            // Recepcionista de salón
            Usuario recepSalon = Usuario.builder()
                    .nombre("recep_salon")
                    .apellido("Vega")
                    .email("recep@klipp-salon.com")
                    .empresa(salon)
                    .rol(RolUsuario.ROLE_RECEPCIONISTA)
                    .passwordHash(passwordEncoder.encode(password))
                    .activo(true).build();
            usuarioRepository.save(recepSalon);

            // Empleadas de salón
            Usuario uLaura = Usuario.builder()
                    .nombre("laura")
                    .apellido("Castillo")
                    .email("laura@klipp-salon.com")
                    .empresa(salon)
                    .rol(RolUsuario.ROLE_EMPLEADO)
                    .passwordHash(passwordEncoder.encode(password))
                    .activo(true).build();
            usuarioRepository.save(uLaura);

            Empleado eLaura = new Empleado();
            eLaura.setUsuario(uLaura);
            eLaura.setEmpresa(salon);
            eLaura.setNombre("Laura Castillo");
            eLaura.setTelefono("70200010");
            eLaura.setCargo(CargoEmpleado.ESTILISTA);
            eLaura.setEspecialidad("Colorimetría · Keratina · Peinados");
            eLaura.setFotoUrl("LC");
            eLaura.setDisponible(true);
            eLaura.setActivo(true);
            empleadoRepository.save(eLaura);

            EmpleadoSucursal esLaura = new EmpleadoSucursal();
            esLaura.setEmpleado(eLaura);
            esLaura.setSucursal(sucSalon);
            esLaura.setActivo(true);
            empleadoSucursalRepository.save(esLaura);

            Usuario uSofia = Usuario.builder()
                    .nombre("sofia")
                    .apellido("Rojas")
                    .email("sofia@klipp-salon.com")
                    .empresa(salon)
                    .rol(RolUsuario.ROLE_EMPLEADO)
                    .passwordHash(passwordEncoder.encode(password))
                    .activo(true).build();
            usuarioRepository.save(uSofia);

            Empleado eSofia = new Empleado();
            eSofia.setUsuario(uSofia);
            eSofia.setEmpresa(salon);
            eSofia.setNombre("Sofía Rojas");
            eSofia.setTelefono("70200011");
            eSofia.setCargo(CargoEmpleado.ESTILISTA);
            eSofia.setEspecialidad("Manicure · Pedicure · Uñas acrílicas");
            eSofia.setFotoUrl("SR");
            eSofia.setDisponible(true);
            eSofia.setActivo(true);
            empleadoRepository.save(eSofia);

            EmpleadoSucursal esSofia = new EmpleadoSucursal();
            esSofia.setEmpleado(eSofia);
            esSofia.setSucursal(sucSalon);
            esSofia.setActivo(true);
            empleadoSucursalRepository.save(esSofia);

            // Clientas de salón
            Cliente cs1 = new Cliente();
            cs1.setEmpresa(salon);
            cs1.setNombre("María López");
            cs1.setTelefono("70300001");
            cs1.setEmail("maria@gmail.com");
            cs1.setDocumento("20010030");
            cs1.setActivo(true);
            clienteRepository.save(cs1);

            Cliente cs2 = new Cliente();
            cs2.setEmpresa(salon);
            cs2.setNombre("Valentina Cruz");
            cs2.setTelefono("70300002");
            cs2.setEmail("valen@gmail.com");
            cs2.setDocumento("20010031");
            cs2.setActivo(true);
            clienteRepository.save(cs2);

            log.info("Empresa SALON 'Klipp Salon Lab' creada con sucursal, usuarios y clientes");
        }

        // ─────────────────────────────────────────────────────
        // EMPRESA 3: VETERINARIA "Klipp Pet Grooming"
        // ─────────────────────────────────────────────────────
        if (empresaRepository.findAll().stream()
                .noneMatch(e -> e.getNit().equals("1000000003"))) {

            Empresa vet = new Empresa();
            vet.setNombre("Klipp Pet Grooming");
            vet.setRazonSocial("Klipp Veterinaria SRL");
            vet.setNit("1000000003");
            vet.setTelefono("70300001");
            vet.setEmail("admin@klipp-vet.com");
            vet.setTipoEmpresa(TipoEmpresa.VETERINARIA);
            vet.setActivo(true);
            empresaRepository.save(vet);

            // Sucursal de veterinaria
            Sucursal sucVet = new Sucursal();
            sucVet.setEmpresa(vet);
            sucVet.setNombre("Sucursal Urubo");
            sucVet.setDireccion("Av. Urubo, Condominio Los Pinos");
            sucVet.setTelefono("70300002");
            sucVet.setActivo(true);
            sucursalRepository.save(sucVet);

            // Admin de veterinaria
            Usuario adminVet = Usuario.builder()
                    .nombre("admin_vet")
                    .apellido("Morales")
                    .email("admin@klipp-vet.com")
                    .empresa(vet)
                    .rol(RolUsuario.ROLE_ADMIN_EMPRESA)
                    .passwordHash(passwordEncoder.encode(password))
                    .activo(true).build();
            usuarioRepository.save(adminVet);

            // Recepcionista de veterinaria
            Usuario recepVet = Usuario.builder()
                    .nombre("recep_vet")
                    .apellido("Lima")
                    .email("recep@klipp-vet.com")
                    .empresa(vet)
                    .rol(RolUsuario.ROLE_RECEPCIONISTA)
                    .passwordHash(passwordEncoder.encode(password))
                    .activo(true).build();
            usuarioRepository.save(recepVet);

            // Empleado veterinaria
            Usuario uDra = Usuario.builder()
                    .nombre("dra_ana")
                    .apellido("Suarez")
                    .email("ana@klipp-vet.com")
                    .empresa(vet)
                    .rol(RolUsuario.ROLE_EMPLEADO)
                    .passwordHash(passwordEncoder.encode(password))
                    .activo(true).build();
            usuarioRepository.save(uDra);

            Empleado eDra = new Empleado();
            eDra.setUsuario(uDra);
            eDra.setEmpresa(vet);
            eDra.setNombre("Dra. Ana Suárez");
            eDra.setTelefono("70300010");
            eDra.setCargo(CargoEmpleado.OTRO);
            eDra.setEspecialidad("Veterinaria general · Grooming canino");
            eDra.setFotoUrl("AS");
            eDra.setDisponible(true);
            eDra.setActivo(true);
            empleadoRepository.save(eDra);

            EmpleadoSucursal esDra = new EmpleadoSucursal();
            esDra.setEmpleado(eDra);
            esDra.setSucursal(sucVet);
            esDra.setActivo(true);
            empleadoSucursalRepository.save(esDra);

            // Especies y razas para veterinaria
            Especie canino = new Especie();
            canino.setNombre("Canino");
            canino.setActivo(true);
            especieRepository.save(canino);

            Especie felino = new Especie();
            felino.setNombre("Felino");
            felino.setActivo(true);
            especieRepository.save(felino);

            Raza labrador = new Raza();
            labrador.setEspecie(canino);
            labrador.setNombre("Labrador Retriever");
            labrador.setActivo(true);
            razaRepository.save(labrador);

            Raza bulldog = new Raza();
            bulldog.setEspecie(canino);
            bulldog.setNombre("Bulldog Francés");
            bulldog.setActivo(true);
            razaRepository.save(bulldog);

            Raza mestizo = new Raza();
            mestizo.setEspecie(canino);
            mestizo.setNombre("Mestizo");
            mestizo.setActivo(true);
            razaRepository.save(mestizo);

            Raza persa = new Raza();
            persa.setEspecie(felino);
            persa.setNombre("Persa");
            persa.setActivo(true);
            razaRepository.save(persa);

            // Clientes veterinaria (dueños de mascotas)
            Cliente cv1 = new Cliente();
            cv1.setEmpresa(vet);
            cv1.setNombre("Roberto Vargas");
            cv1.setTelefono("70400001");
            cv1.setEmail("roberto@gmail.com");
            cv1.setDocumento("30010040");
            cv1.setActivo(true);
            clienteRepository.save(cv1);

            // Mascota del cliente
            Mascota m1 = new Mascota();
            m1.setCliente(cv1);
            m1.setRaza(labrador);
            m1.setNombre("Max");
            m1.setNotasEspeciales("Alérgico a algunos shampoos. Usar solo productos hipoalergénicos.");
            m1.setActivo(true);
            mascotaRepository.save(m1);

            Mascota m2 = new Mascota();
            m2.setCliente(cv1);
            m2.setRaza(bulldog);
            m2.setNombre("Rocky");
            m2.setNotasEspeciales("Manejo cuidadoso. Es juguetón pero puede morder.");
            m2.setActivo(true);
            mascotaRepository.save(m2);

            log.info(" Empresa VETERINARIA 'Klipp Pet Grooming' creada con sucursal, usuarios, razas y cliente");
        }
        log.info(" DataInitializer completado.");
        // ─────────────────────────────────────────────────────
        // PRODUCTOS — EMPRESA 1: BARBERÍA "Klipp Barber Studio"
        // ─────────────────────────────────────────────────────
        empresaRepository.findAll().stream()
                .filter(e -> e.getNit().equals("1000000001"))
                .findFirst()
                .ifPresent(barberia -> {
                    if (productoRepository.findByEmpresa(barberia).isEmpty()) {

                        productoRepository.save(Producto.builder()
                                .empresa(barberia)
                                .nombre("Aceite para barba")
                                .descripcion("Hidrata el vello facial y la piel, evitando la sequedad y la descamación.")
                                .precioVenta(new BigDecimal("50.00"))
                                .activo(true).build());

                        productoRepository.save(Producto.builder()
                                .empresa(barberia)
                                .nombre("Bálsamo para barba")
                                .descripcion("Ayuda a dar forma, estilizar y fijar la barba rebelde con acabado natural.")
                                .precioVenta(new BigDecimal("55.00"))
                                .activo(true).build());

                        productoRepository.save(Producto.builder()
                                .empresa(barberia)
                                .nombre("Champú para barba")
                                .descripcion("Limpia sin resecar el vello facial ni la piel subyacente.")
                                .precioVenta(new BigDecimal("45.00"))
                                .activo(true).build());

                        productoRepository.save(Producto.builder()
                                .empresa(barberia)
                                .nombre("Cera moldeadora")
                                .descripcion("Ideal para moldear, dar textura y fijar peinados con control fuerte.")
                                .precioVenta(new BigDecimal("40.00"))
                                .activo(true).build());

                        productoRepository.save(Producto.builder()
                                .empresa(barberia)
                                .nombre("Polvos voluminizadores")
                                .descripcion("Crean peinados modernos con mucho volumen y acabado mate natural.")
                                .precioVenta(new BigDecimal("48.00"))
                                .activo(true).build());

                        productoRepository.save(Producto.builder()
                                .empresa(barberia)
                                .nombre("Tónico capilar")
                                .descripcion("Brinda textura y prepara el cabello antes del secado con soplete.")
                                .precioVenta(new BigDecimal("60.00"))
                                .activo(true).build());

                        productoRepository.save(Producto.builder()
                                .empresa(barberia)
                                .nombre("Espuma de afeitar")
                                .descripcion("Suaviza el vello y protege la piel durante el afeitado con navaja.")
                                .precioVenta(new BigDecimal("35.00"))
                                .activo(true).build());

                        productoRepository.save(Producto.builder()
                                .empresa(barberia)
                                .nombre("After-shave loción")
                                .descripcion("Calma la irritación y cierra los poros tras el afeitado. Aroma fresco.")
                                .precioVenta(new BigDecimal("55.00"))
                                .activo(true).build());

                        log.info("Productos BARBERÍA 'Klipp Barber Studio' creados");
                    }
                });

        // ─────────────────────────────────────────────────────
        // PRODUCTOS — EMPRESA 2: SALÓN "Klipp Salon Lab"
        // ─────────────────────────────────────────────────────
        empresaRepository.findAll().stream()
                .filter(e -> e.getNit().equals("1000000002"))
                .findFirst()
                .ifPresent(salon -> {
                    if (productoRepository.findByEmpresa(salon).isEmpty()) {

                        productoRepository.save(Producto.builder()
                                .empresa(salon)
                                .nombre("Shampoo hidratante")
                                .descripcion("Shampoo de uso profesional con keratina y aceite de argán para cabello seco.")
                                .precioVenta(new BigDecimal("75.00"))
                                .activo(true).build());

                        productoRepository.save(Producto.builder()
                                .empresa(salon)
                                .nombre("Acondicionador reparador")
                                .descripcion("Acondicionador con proteínas de seda que repara el cabello dañado por el calor.")
                                .precioVenta(new BigDecimal("70.00"))
                                .activo(true).build());

                        productoRepository.save(Producto.builder()
                                .empresa(salon)
                                .nombre("Mascarilla capilar nutritiva")
                                .descripcion("Tratamiento intensivo de nutrición profunda para cabellos teñidos o con keratina.")
                                .precioVenta(new BigDecimal("90.00"))
                                .activo(true).build());

                        productoRepository.save(Producto.builder()
                                .empresa(salon)
                                .nombre("Sérum antifrizz")
                                .descripcion("Controla el frizz y aporta brillo sin dejar residuo graso. Uso diario.")
                                .precioVenta(new BigDecimal("85.00"))
                                .activo(true).build());

                        productoRepository.save(Producto.builder()
                                .empresa(salon)
                                .nombre("Esmalte de uñas semipermanente")
                                .descripcion("Esmalte gel de larga duración, secado UV. Pack de color a elección.")
                                .precioVenta(new BigDecimal("40.00"))
                                .activo(true).build());

                        productoRepository.save(Producto.builder()
                                .empresa(salon)
                                .nombre("Kit de manicure personal")
                                .descripcion("Set con lima, empujador de cutículas, tijera y alicate de uñas profesional.")
                                .precioVenta(new BigDecimal("65.00"))
                                .activo(true).build());

                        productoRepository.save(Producto.builder()
                                .empresa(salon)
                                .nombre("Crema de manos hidratante")
                                .descripcion("Crema con manteca de karité y vitamina E. Absorción rápida, sin residuo.")
                                .precioVenta(new BigDecimal("45.00"))
                                .activo(true).build());

                        productoRepository.save(Producto.builder()
                                .empresa(salon)
                                .nombre("Tinte capilar profesional")
                                .descripcion("Tinte en crema de cobertura total, paleta de 30 tonos. Por unidad.")
                                .precioVenta(new BigDecimal("55.00"))
                                .activo(true).build());

                        log.info("    Productos SALÓN 'Klipp Salon Lab' creados");
                    }
                });

        // ─────────────────────────────────────────────────────
        // PRODUCTOS — EMPRESA 3: VETERINARIA "Klipp Pet Grooming"
        // ─────────────────────────────────────────────────────
        empresaRepository.findAll().stream()
                .filter(e -> e.getNit().equals("1000000003"))
                .findFirst()
                .ifPresent(vet -> {
                    if (productoRepository.findByEmpresa(vet).isEmpty()) {

                        productoRepository.save(Producto.builder()
                                .empresa(vet)
                                .nombre("Shampoo para mascotas")
                                .descripcion("Shampoo suave con pH balanceado para perros y gatos. Aroma neutro.")
                                .precioVenta(new BigDecimal("55.00"))
                                .activo(true).build());

                        productoRepository.save(Producto.builder()
                                .empresa(vet)
                                .nombre("Shampoo hipoalergénico")
                                .descripcion("Fórmula sin fragancia ni colorantes, ideal para pieles sensibles o alérgicas.")
                                .precioVenta(new BigDecimal("70.00"))
                                .activo(true).build());

                        productoRepository.save(Producto.builder()
                                .empresa(vet)
                                .nombre("Acondicionador desmata​ntes")
                                .descripcion("Facilita el desenredo del pelaje largo, reduce el nudo y aporta brillo.")
                                .precioVenta(new BigDecimal("60.00"))
                                .activo(true).build());

                        productoRepository.save(Producto.builder()
                                .empresa(vet)
                                .nombre("Antipulgas pipeta")
                                .descripcion("Tratamiento tópico mensual contra pulgas, garrapatas y mosquitos. Por unidad.")
                                .precioVenta(new BigDecimal("65.00"))
                                .activo(true).build());

                        productoRepository.save(Producto.builder()
                                .empresa(vet)
                                .nombre("Collar antipulgas")
                                .descripcion("Collar de protección continua por 8 meses contra pulgas y garrapatas.")
                                .precioVenta(new BigDecimal("90.00"))
                                .activo(true).build());

                        productoRepository.save(Producto.builder()
                                .empresa(vet)
                                .nombre("Limpiador de oídos")
                                .descripcion("Solución otológica para limpieza y prevención de infecciones en oídos.")
                                .precioVenta(new BigDecimal("40.00"))
                                .activo(true).build());

                        productoRepository.save(Producto.builder()
                                .empresa(vet)
                                .nombre("Cepillo deslanador")
                                .descripcion("Cepillo profesional con púas de acero inoxidable para control de muda.")
                                .precioVenta(new BigDecimal("75.00"))
                                .activo(true).build());

                        productoRepository.save(Producto.builder()
                                .empresa(vet)
                                .nombre("Cortaúñas para mascotas")
                                .descripcion("Cortaúñas con tope de seguridad y mango antideslizante. Tamaño mediano.")
                                .precioVenta(new BigDecimal("45.00"))
                                .activo(true).build());

                        log.info("    Productos VETERINARIA 'Klipp Pet Grooming' creados");
                    }
                });
        // ─────────────────────────────────────────────────────
        // SERVICIOS — EMPRESA 1: BARBERÍA "Klipp Barber Studio"
        // ─────────────────────────────────────────────────────
        empresaRepository.findAll().stream()
                .filter(e -> e.getNit().equals("1000000001"))
                .findFirst()
                .ifPresent(barberia -> {
                    if (servicioRepository.findByEmpresa(barberia).isEmpty()) {

                        servicioRepository.save(Servicio.builder()
                                .empresa(barberia)
                                .nombre("Corte clásico")
                                .descripcion("Corte de cabello clásico con tijera o máquina, lavado y secado incluido.")
                                .destinatario(TipoDestinatarioServicio.HUMANO)
                                .categoria(CategoriaServicio.BARBERIA)
                                .duracionMinutos(30)
                                .precioBase(new BigDecimal("50.00"))
                                .activo(true).build());

                        servicioRepository.save(Servicio.builder()
                                .empresa(barberia)
                                .nombre("Skin fade")
                                .descripcion("Degradado a piel con máquina, acabado limpio en cuello y patillas.")
                                .destinatario(TipoDestinatarioServicio.HUMANO)
                                .categoria(CategoriaServicio.BARBERIA)
                                .duracionMinutos(40)
                                .precioBase(new BigDecimal("60.00"))
                                .activo(true).build());

                        servicioRepository.save(Servicio.builder()
                                .empresa(barberia)
                                .nombre("Corte + barba")
                                .descripcion("Corte de cabello más perfilado y arreglo completo de barba.")
                                .destinatario(TipoDestinatarioServicio.HUMANO)
                                .categoria(CategoriaServicio.BARBERIA)
                                .duracionMinutos(50)
                                .precioBase(new BigDecimal("80.00"))
                                .activo(true).build());

                        servicioRepository.save(Servicio.builder()
                                .empresa(barberia)
                                .nombre("Afeitado navaja")
                                .descripcion("Afeitado tradicional con navaja, toalla caliente y loción after-shave.")
                                .destinatario(TipoDestinatarioServicio.HUMANO)
                                .categoria(CategoriaServicio.BARBERIA)
                                .duracionMinutos(30)
                                .precioBase(new BigDecimal("45.00"))
                                .activo(true).build());

                        servicioRepository.save(Servicio.builder()
                                .empresa(barberia)
                                .nombre("Diseño de barba")
                                .descripcion("Perfilado, modelado y definición de líneas de barba con cera de acabado.")
                                .destinatario(TipoDestinatarioServicio.HUMANO)
                                .categoria(CategoriaServicio.BARBERIA)
                                .duracionMinutos(25)
                                .precioBase(new BigDecimal("40.00"))
                                .activo(true).build());

                        servicioRepository.save(Servicio.builder()
                                .empresa(barberia)
                                .nombre("Color / tinte capilar")
                                .descripcion("Aplicación de color o tinte en cabello, incluye lavado y secado.")
                                .destinatario(TipoDestinatarioServicio.HUMANO)
                                .categoria(CategoriaServicio.BARBERIA)
                                .duracionMinutos(60)
                                .precioBase(new BigDecimal("90.00"))
                                .activo(true).build());

                        log.info("    Servicios BARBERÍA 'Klipp Barber Studio' creados");
                    }
                });

        // ─────────────────────────────────────────────────────
        // SERVICIOS — EMPRESA 2: SALÓN "Klipp Salon Lab"
        // ─────────────────────────────────────────────────────
        empresaRepository.findAll().stream()
                .filter(e -> e.getNit().equals("1000000002"))
                .findFirst()
                .ifPresent(salon -> {
                    if (servicioRepository.findByEmpresa(salon).isEmpty()) {

                        servicioRepository.save(Servicio.builder()
                                .empresa(salon)
                                .nombre("Corte femenino")
                                .descripcion("Corte de cabello para dama, lavado, secado y peinado incluidos.")
                                .destinatario(TipoDestinatarioServicio.HUMANO)
                                .categoria(CategoriaServicio.PELUQUERIA)
                                .duracionMinutos(45)
                                .precioBase(new BigDecimal("70.00"))
                                .activo(true).build());

                        servicioRepository.save(Servicio.builder()
                                .empresa(salon)
                                .nombre("Colorimetría completa")
                                .descripcion("Tinte, balayage o mechas; incluye tratamiento de hidratación post-color.")
                                .destinatario(TipoDestinatarioServicio.HUMANO)
                                .categoria(CategoriaServicio.PELUQUERIA)
                                .duracionMinutos(120)
                                .precioBase(new BigDecimal("220.00"))
                                .activo(true).build());

                        servicioRepository.save(Servicio.builder()
                                .empresa(salon)
                                .nombre("Keratina / alisado")
                                .descripcion("Tratamiento de keratina brasileña para alisar y nutrir el cabello.")
                                .destinatario(TipoDestinatarioServicio.HUMANO)
                                .categoria(CategoriaServicio.ESTETICA)
                                .duracionMinutos(150)
                                .precioBase(new BigDecimal("280.00"))
                                .activo(true).build());

                        servicioRepository.save(Servicio.builder()
                                .empresa(salon)
                                .nombre("Peinado de fiesta")
                                .descripcion("Peinado elaborado para eventos, recogidos, ondas o planchado.")
                                .destinatario(TipoDestinatarioServicio.HUMANO)
                                .categoria(CategoriaServicio.PELUQUERIA)
                                .duracionMinutos(60)
                                .precioBase(new BigDecimal("100.00"))
                                .activo(true).build());

                        servicioRepository.save(Servicio.builder()
                                .empresa(salon)
                                .nombre("Manicure clásico")
                                .descripcion("Limpieza, limado, cutículas y esmaltado de uñas de manos.")
                                .destinatario(TipoDestinatarioServicio.HUMANO)
                                .categoria(CategoriaServicio.ESTETICA)
                                .duracionMinutos(40)
                                .precioBase(new BigDecimal("45.00"))
                                .activo(true).build());

                        servicioRepository.save(Servicio.builder()
                                .empresa(salon)
                                .nombre("Pedicure completo")
                                .descripcion("Exfoliación, hidratación, limado y esmaltado de uñas de pies.")
                                .destinatario(TipoDestinatarioServicio.HUMANO)
                                .categoria(CategoriaServicio.ESTETICA)
                                .duracionMinutos(50)
                                .precioBase(new BigDecimal("55.00"))
                                .activo(true).build());

                        servicioRepository.save(Servicio.builder()
                                .empresa(salon)
                                .nombre("Uñas acrílicas")
                                .descripcion("Colocación de uñas acrílicas con diseño incluido, manos completas.")
                                .destinatario(TipoDestinatarioServicio.HUMANO)
                                .categoria(CategoriaServicio.ESTETICA)
                                .duracionMinutos(90)
                                .precioBase(new BigDecimal("130.00"))
                                .activo(true).build());

                        log.info("    Servicios SALÓN 'Klipp Salon Lab' creados");
                    }
                });

        // ─────────────────────────────────────────────────────
        // SERVICIOS — EMPRESA 3: VETERINARIA "Klipp Pet Grooming"
        // ─────────────────────────────────────────────────────
        empresaRepository.findAll().stream()
                .filter(e -> e.getNit().equals("1000000003"))
                .findFirst()
                .ifPresent(vet -> {
                    if (servicioRepository.findByEmpresa(vet).isEmpty()) {

                        servicioRepository.save(Servicio.builder()
                                .empresa(vet)
                                .nombre("Baño y secado")
                                .descripcion("Baño con shampoo especializado, secado y cepillado básico.")
                                .destinatario(TipoDestinatarioServicio.MASCOTA)
                                .categoria(CategoriaServicio.GROOMING)
                                .duracionMinutos(60)
                                .precioBase(new BigDecimal("80.00"))
                                .activo(true).build());

                        servicioRepository.save(Servicio.builder()
                                .empresa(vet)
                                .nombre("Baño + corte de pelo")
                                .descripcion("Baño completo más corte de pelo según raza y preferencia del dueño.")
                                .destinatario(TipoDestinatarioServicio.MASCOTA)
                                .categoria(CategoriaServicio.GROOMING)
                                .duracionMinutos(90)
                                .precioBase(new BigDecimal("130.00"))
                                .activo(true).build());

                        servicioRepository.save(Servicio.builder()
                                .empresa(vet)
                                .nombre("Baño hipoalergénico")
                                .descripcion("Baño con productos hipoalergénicos, indicado para pieles sensibles o alérgicas.")
                                .destinatario(TipoDestinatarioServicio.MASCOTA)
                                .categoria(CategoriaServicio.GROOMING)
                                .duracionMinutos(70)
                                .precioBase(new BigDecimal("100.00"))
                                .activo(true).build());

                        servicioRepository.save(Servicio.builder()
                                .empresa(vet)
                                .nombre("Corte de uñas")
                                .descripcion("Corte y limado de uñas de las cuatro patas.")
                                .destinatario(TipoDestinatarioServicio.MASCOTA)
                                .categoria(CategoriaServicio.HIGIENE)
                                .duracionMinutos(15)
                                .precioBase(new BigDecimal("30.00"))
                                .activo(true).build());

                        servicioRepository.save(Servicio.builder()
                                .empresa(vet)
                                .nombre("Limpieza de oídos")
                                .descripcion("Limpieza profunda de conductos auditivos con solución veterinaria.")
                                .destinatario(TipoDestinatarioServicio.MASCOTA)
                                .categoria(CategoriaServicio.HIGIENE)
                                .duracionMinutos(15)
                                .precioBase(new BigDecimal("35.00"))
                                .activo(true).build());

                        servicioRepository.save(Servicio.builder()
                                .empresa(vet)
                                .nombre("Desparasitación externa")
                                .descripcion("Aplicación de producto antipulgas y garrapatas de efecto prolongado.")
                                .destinatario(TipoDestinatarioServicio.MASCOTA)
                                .categoria(CategoriaServicio.VETERINARIA)
                                .duracionMinutos(20)
                                .precioBase(new BigDecimal("60.00"))
                                .activo(true).build());

                        servicioRepository.save(Servicio.builder()
                                .empresa(vet)
                                .nombre("Consulta veterinaria general")
                                .descripcion("Revisión clínica general, diagnóstico y orientación médica básica.")
                                .destinatario(TipoDestinatarioServicio.MASCOTA)
                                .categoria(CategoriaServicio.VETERINARIA)
                                .duracionMinutos(30)
                                .precioBase(new BigDecimal("90.00"))
                                .activo(true).build());

                        log.info("    Servicios VETERINARIA 'Klipp Pet Grooming' creados");
                    }
                });
        // ─────────────────────────────────────────────────────
// CITAS — EMPRESA 1: BARBERÍA "Klipp Barber Studio"
// ─────────────────────────────────────────────────────
        empresaRepository.findAll().stream()
                .filter(e -> e.getNit().equals("1000000001"))
                .findFirst()
                .ifPresent(barberia -> {
                    if (agendaEventoRepository.findBySucursal_Empresa(barberia).isEmpty()) {

                        Sucursal sucBarber = sucursalRepository.findByEmpresa(barberia).get(0);
                        Usuario recepBarber = usuarioRepository.findByEmail("recep@klipp-barber.com").orElseThrow();
                        Cliente juan   = clienteRepository.findByEmailAndEmpresa("juan@gmail.com", barberia).orElseThrow();
                        Cliente diego  = clienteRepository.findByEmailAndEmpresa("diego@gmail.com", barberia).orElseThrow();
                        Empleado miguel  = empleadoRepository.findByUsuario_Email("miguel@klipp-barber.com").orElseThrow();
                        Empleado carlos  = empleadoRepository.findByUsuario_Email("carlos@klipp-barber.com").orElseThrow();
                        Servicio corteClasico   = servicioRepository.findByEmpresaAndNombre(barberia, "Corte clásico").orElseThrow();
                        Servicio skinFade       = servicioRepository.findByEmpresaAndNombre(barberia, "Skin fade").orElseThrow();
                        Servicio corteBarba     = servicioRepository.findByEmpresaAndNombre(barberia, "Corte + barba").orElseThrow();
                        Servicio disenoBarba    = servicioRepository.findByEmpresaAndNombre(barberia, "Diseño de barba").orElseThrow();

                        // Cita 1 — Juan, corte clásico con Miguel, CONFIRMADO (ayer)
                        AgendaEvento ev1 = new AgendaEvento();
                        ev1.setCliente(juan);
                        ev1.setSucursal(sucBarber);
                        ev1.setCreadoPorUsuario(recepBarber);
                        ev1.setTipoEvento(TipoEvento.CITA);
                        ev1.setEstado(EstadoEvento.FINALIZADO);
                        ev1.setInicio(OffsetDateTime.now().minusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0));
                        ev1.setFin(OffsetDateTime.now().minusDays(1).withHour(9).withMinute(30).withSecond(0).withNano(0));
                        ev1.setNotas("Cliente frecuente. Prefiere acabado limpio en nuca.");
                        agendaEventoRepository.save(ev1);

                        AgendaEventoDetalle det1 = new AgendaEventoDetalle();
                        det1.setAgendaEvento(ev1);
                        det1.setServicio(corteClasico);
                        det1.setDuracionEstimadaMinutos(corteClasico.getDuracionMinutos());
                        det1.setPrecioAcordado(corteClasico.getPrecioBase());
                        agendaEventoDetalleRepository.save(det1);

                        AgendaEventoEmpleado emp1 = new AgendaEventoEmpleado();
                        emp1.setAgendaEvento(ev1);
                        emp1.setEmpleado(miguel);
                        emp1.setRolEnEvento(RolEmpleadoEvento.RESPONSABLE);
                        agendaEventoEmpleadoRepository.save(emp1);

                        // Cita 2 — Diego, skin fade con Carlos, CONFIRMADO (hoy mañana)
                        AgendaEvento ev2 = new AgendaEvento();
                        ev2.setCliente(diego);
                        ev2.setSucursal(sucBarber);
                        ev2.setCreadoPorUsuario(recepBarber);
                        ev2.setTipoEvento(TipoEvento.CITA);
                        ev2.setEstado(EstadoEvento.CONFIRMADO);
                        ev2.setInicio(OffsetDateTime.now().withHour(10).withMinute(0).withSecond(0).withNano(0));
                        ev2.setFin(OffsetDateTime.now().withHour(10).withMinute(40).withSecond(0).withNano(0));
                        ev2.setNotas("Degradado bajo, sin diseño en patillas.");
                        agendaEventoRepository.save(ev2);

                        AgendaEventoDetalle det2 = new AgendaEventoDetalle();
                        det2.setAgendaEvento(ev2);
                        det2.setServicio(skinFade);
                        det2.setDuracionEstimadaMinutos(skinFade.getDuracionMinutos());
                        det2.setPrecioAcordado(skinFade.getPrecioBase());
                        agendaEventoDetalleRepository.save(det2);

                        AgendaEventoEmpleado emp2 = new AgendaEventoEmpleado();
                        emp2.setAgendaEvento(ev2);
                        emp2.setEmpleado(carlos);
                        emp2.setRolEnEvento(RolEmpleadoEvento.RESPONSABLE);
                        agendaEventoEmpleadoRepository.save(emp2);

                        // Cita 3 — Juan, corte + barba con Miguel, PENDIENTE (mañana)
                        AgendaEvento ev3 = new AgendaEvento();
                        ev3.setCliente(juan);
                        ev3.setSucursal(sucBarber);
                        ev3.setCreadoPorUsuario(recepBarber);
                        ev3.setTipoEvento(TipoEvento.CITA);
                        ev3.setEstado(EstadoEvento.PENDIENTE);
                        ev3.setInicio(OffsetDateTime.now().plusDays(1).withHour(11).withMinute(0).withSecond(0).withNano(0));
                        ev3.setFin(OffsetDateTime.now().plusDays(1).withHour(11).withMinute(50).withSecond(0).withNano(0));
                        ev3.setNotas("Llevar foto de referencia para el diseño de barba.");
                        agendaEventoRepository.save(ev3);

                        AgendaEventoDetalle det3 = new AgendaEventoDetalle();
                        det3.setAgendaEvento(ev3);
                        det3.setServicio(corteBarba);
                        det3.setDuracionEstimadaMinutos(corteBarba.getDuracionMinutos());
                        det3.setPrecioAcordado(corteBarba.getPrecioBase());
                        agendaEventoDetalleRepository.save(det3);

                        AgendaEventoEmpleado emp3 = new AgendaEventoEmpleado();
                        emp3.setAgendaEvento(ev3);
                        emp3.setEmpleado(miguel);
                        emp3.setRolEnEvento(RolEmpleadoEvento.RESPONSABLE);
                        agendaEventoEmpleadoRepository.save(emp3);

                        // Cita 4 — Diego, diseño de barba con Carlos, PENDIENTE (pasado mañana)
                        AgendaEvento ev4 = new AgendaEvento();
                        ev4.setCliente(diego);
                        ev4.setSucursal(sucBarber);
                        ev4.setCreadoPorUsuario(recepBarber);
                        ev4.setTipoEvento(TipoEvento.CITA);
                        ev4.setEstado(EstadoEvento.PENDIENTE);
                        ev4.setInicio(OffsetDateTime.now().plusDays(2).withHour(14).withMinute(0).withSecond(0).withNano(0));
                        ev4.setFin(OffsetDateTime.now().plusDays(2).withHour(14).withMinute(25).withSecond(0).withNano(0));
                        agendaEventoRepository.save(ev4);

                        AgendaEventoDetalle det4 = new AgendaEventoDetalle();
                        det4.setAgendaEvento(ev4);
                        det4.setServicio(disenoBarba);
                        det4.setDuracionEstimadaMinutos(disenoBarba.getDuracionMinutos());
                        det4.setPrecioAcordado(disenoBarba.getPrecioBase());
                        agendaEventoDetalleRepository.save(det4);

                        AgendaEventoEmpleado emp4 = new AgendaEventoEmpleado();
                        emp4.setAgendaEvento(ev4);
                        emp4.setEmpleado(carlos);
                        emp4.setRolEnEvento(RolEmpleadoEvento.RESPONSABLE);
                        agendaEventoEmpleadoRepository.save(emp4);

                        log.info("    Citas BARBERÍA 'Klipp Barber Studio' creadas");
                    }
                });

// ─────────────────────────────────────────────────────
// CITAS — EMPRESA 2: SALÓN "Klipp Salon Lab"
// ─────────────────────────────────────────────────────
        empresaRepository.findAll().stream()
                .filter(e -> e.getNit().equals("1000000002"))
                .findFirst()
                .ifPresent(salon -> {
                    if (agendaEventoRepository.findBySucursal_Empresa(salon).isEmpty()) {

                        Sucursal sucSalon  = sucursalRepository.findByEmpresa(salon).get(0);
                        Usuario recepSalon = usuarioRepository.findByEmail("recep@klipp-salon.com").orElseThrow();
                        Cliente maria  = clienteRepository.findByEmailAndEmpresa("maria@gmail.com", salon).orElseThrow();
                        Cliente valen  = clienteRepository.findByEmailAndEmpresa("valen@gmail.com", salon).orElseThrow();
                        Empleado laura = empleadoRepository.findByUsuario_Email("laura@klipp-salon.com").orElseThrow();
                        Empleado sofia = empleadoRepository.findByUsuario_Email("sofia@klipp-salon.com").orElseThrow();
                        Servicio corteFem    = servicioRepository.findByEmpresaAndNombre(salon, "Corte femenino").orElseThrow();
                        Servicio colorimetria = servicioRepository.findByEmpresaAndNombre(salon, "Colorimetría completa").orElseThrow();
                        Servicio manicure    = servicioRepository.findByEmpresaAndNombre(salon, "Manicure clásico").orElseThrow();
                        Servicio pedicure    = servicioRepository.findByEmpresaAndNombre(salon, "Pedicure completo").orElseThrow();

                        // Cita 1 — María, colorimetría con Laura, FINALIZADO (ayer)
                        AgendaEvento ev5 = new AgendaEvento();
                        ev5.setCliente(maria);
                        ev5.setSucursal(sucSalon);
                        ev5.setCreadoPorUsuario(recepSalon);
                        ev5.setTipoEvento(TipoEvento.CITA);
                        ev5.setEstado(EstadoEvento.FINALIZADO);
                        ev5.setInicio(OffsetDateTime.now().minusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0));
                        ev5.setFin(OffsetDateTime.now().minusDays(1).withHour(12).withMinute(0).withSecond(0).withNano(0));
                        ev5.setNotas("Tono castaño dorado, balayage suave. Cliente trajo referencia en foto.");
                        agendaEventoRepository.save(ev5);

                        AgendaEventoDetalle det5 = new AgendaEventoDetalle();
                        det5.setAgendaEvento(ev5);
                        det5.setServicio(colorimetria);
                        det5.setDuracionEstimadaMinutos(colorimetria.getDuracionMinutos());
                        det5.setPrecioAcordado(colorimetria.getPrecioBase());
                        agendaEventoDetalleRepository.save(det5);

                        AgendaEventoEmpleado emp5 = new AgendaEventoEmpleado();
                        emp5.setAgendaEvento(ev5);
                        emp5.setEmpleado(laura);
                        emp5.setRolEnEvento(RolEmpleadoEvento.RESPONSABLE);
                        agendaEventoEmpleadoRepository.save(emp5);

                        // Cita 2 — Valentina, manicure + pedicure con Sofía, CONFIRMADO (hoy tarde)
                        AgendaEvento ev6 = new AgendaEvento();
                        ev6.setCliente(valen);
                        ev6.setSucursal(sucSalon);
                        ev6.setCreadoPorUsuario(recepSalon);
                        ev6.setTipoEvento(TipoEvento.CITA);
                        ev6.setEstado(EstadoEvento.CONFIRMADO);
                        ev6.setInicio(OffsetDateTime.now().withHour(15).withMinute(0).withSecond(0).withNano(0));
                        ev6.setFin(OffsetDateTime.now().withHour(16).withMinute(30).withSecond(0).withNano(0));
                        ev6.setNotas("Esmalte nude para manos, rojo para pies.");
                        agendaEventoRepository.save(ev6);

                        AgendaEventoDetalle det6a = new AgendaEventoDetalle();
                        det6a.setAgendaEvento(ev6);
                        det6a.setServicio(manicure);
                        det6a.setDuracionEstimadaMinutos(manicure.getDuracionMinutos());
                        det6a.setPrecioAcordado(manicure.getPrecioBase());
                        agendaEventoDetalleRepository.save(det6a);

                        AgendaEventoDetalle det6b = new AgendaEventoDetalle();
                        det6b.setAgendaEvento(ev6);
                        det6b.setServicio(pedicure);
                        det6b.setDuracionEstimadaMinutos(pedicure.getDuracionMinutos());
                        det6b.setPrecioAcordado(pedicure.getPrecioBase());
                        agendaEventoDetalleRepository.save(det6b);

                        AgendaEventoEmpleado emp6 = new AgendaEventoEmpleado();
                        emp6.setAgendaEvento(ev6);
                        emp6.setEmpleado(sofia);
                        emp6.setRolEnEvento(RolEmpleadoEvento.RESPONSABLE);
                        agendaEventoEmpleadoRepository.save(emp6);

                        // Cita 3 — María, corte femenino con Laura, PENDIENTE (mañana)
                        AgendaEvento ev7 = new AgendaEvento();
                        ev7.setCliente(maria);
                        ev7.setSucursal(sucSalon);
                        ev7.setCreadoPorUsuario(recepSalon);
                        ev7.setTipoEvento(TipoEvento.CITA);
                        ev7.setEstado(EstadoEvento.PENDIENTE);
                        ev7.setInicio(OffsetDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0));
                        ev7.setFin(OffsetDateTime.now().plusDays(1).withHour(9).withMinute(45).withSecond(0).withNano(0));
                        agendaEventoRepository.save(ev7);

                        AgendaEventoDetalle det7 = new AgendaEventoDetalle();
                        det7.setAgendaEvento(ev7);
                        det7.setServicio(corteFem);
                        det7.setDuracionEstimadaMinutos(corteFem.getDuracionMinutos());
                        det7.setPrecioAcordado(corteFem.getPrecioBase());
                        agendaEventoDetalleRepository.save(det7);

                        AgendaEventoEmpleado emp7 = new AgendaEventoEmpleado();
                        emp7.setAgendaEvento(ev7);
                        emp7.setEmpleado(laura);
                        emp7.setRolEnEvento(RolEmpleadoEvento.RESPONSABLE);
                        agendaEventoEmpleadoRepository.save(emp7);

                        log.info("Citas SALÓN 'Klipp Salon Lab' creadas");
                    }
                });

// ─────────────────────────────────────────────────────
// CITAS — EMPRESA 3: VETERINARIA "Klipp Pet Grooming"
// ─────────────────────────────────────────────────────
        empresaRepository.findAll().stream()
                .filter(e -> e.getNit().equals("1000000003"))
                .findFirst()
                .ifPresent(vet -> {
                    if (agendaEventoRepository.findBySucursal_Empresa(vet).isEmpty()) {

                        Sucursal sucVet  = sucursalRepository.findByEmpresa(vet).get(0);
                        Usuario recepVet = usuarioRepository.findByEmail("recep@klipp-vet.com").orElseThrow();
                        Cliente roberto  = clienteRepository.findByEmailAndEmpresa("roberto@gmail.com", vet).orElseThrow();
                        Empleado dra     = empleadoRepository.findByUsuario_Email("ana@klipp-vet.com").orElseThrow();
                        Mascota max      = mascotaRepository.findByClienteAndNombre(roberto, "Max").orElseThrow();
                        Mascota rocky    = mascotaRepository.findByClienteAndNombre(roberto, "Rocky").orElseThrow();
                        Servicio bano           = servicioRepository.findByEmpresaAndNombre(vet, "Baño y secado").orElseThrow();
                        Servicio banoCorte      = servicioRepository.findByEmpresaAndNombre(vet, "Baño + corte de pelo").orElseThrow();
                        Servicio banoHipo       = servicioRepository.findByEmpresaAndNombre(vet, "Baño hipoalergénico").orElseThrow();
                        Servicio consulta       = servicioRepository.findByEmpresaAndNombre(vet, "Consulta veterinaria general").orElseThrow();

                        // Cita 1 — Max (labrador), baño hipoalergénico con Dra. Ana, FINALIZADO (ayer)
                        AgendaEvento ev8 = new AgendaEvento();
                        ev8.setCliente(roberto);
                        ev8.setMascota(max);
                        ev8.setSucursal(sucVet);
                        ev8.setCreadoPorUsuario(recepVet);
                        ev8.setTipoEvento(TipoEvento.CITA);
                        ev8.setEstado(EstadoEvento.FINALIZADO);
                        ev8.setInicio(OffsetDateTime.now().minusDays(1).withHour(8).withMinute(0).withSecond(0).withNano(0));
                        ev8.setFin(OffsetDateTime.now().minusDays(1).withHour(9).withMinute(10).withSecond(0).withNano(0));
                        ev8.setNotas("Max es alérgico a shampoos convencionales. Usar solo línea hipoalergénica.");
                        agendaEventoRepository.save(ev8);

                        AgendaEventoDetalle det8 = new AgendaEventoDetalle();
                        det8.setAgendaEvento(ev8);
                        det8.setServicio(banoHipo);
                        det8.setDuracionEstimadaMinutos(banoHipo.getDuracionMinutos());
                        det8.setPrecioAcordado(banoHipo.getPrecioBase());
                        agendaEventoDetalleRepository.save(det8);

                        AgendaEventoEmpleado emp8 = new AgendaEventoEmpleado();
                        emp8.setAgendaEvento(ev8);
                        emp8.setEmpleado(dra);
                        emp8.setRolEnEvento(RolEmpleadoEvento.RESPONSABLE);
                        agendaEventoEmpleadoRepository.save(emp8);

                        // Cita 2 — Rocky (bulldog), baño + corte con Dra. Ana, CONFIRMADO (hoy)
                        AgendaEvento ev9 = new AgendaEvento();
                        ev9.setCliente(roberto);
                        ev9.setMascota(rocky);
                        ev9.setSucursal(sucVet);
                        ev9.setCreadoPorUsuario(recepVet);
                        ev9.setTipoEvento(TipoEvento.CITA);
                        ev9.setEstado(EstadoEvento.CONFIRMADO);
                        ev9.setInicio(OffsetDateTime.now().withHour(11).withMinute(0).withSecond(0).withNano(0));
                        ev9.setFin(OffsetDateTime.now().withHour(12).withMinute(30).withSecond(0).withNano(0));
                        ev9.setNotas("Rocky es juguetón y puede morder. Usar bozal preventivo.");
                        agendaEventoRepository.save(ev9);

                        AgendaEventoDetalle det9 = new AgendaEventoDetalle();
                        det9.setAgendaEvento(ev9);
                        det9.setServicio(banoCorte);
                        det9.setDuracionEstimadaMinutos(banoCorte.getDuracionMinutos());
                        det9.setPrecioAcordado(banoCorte.getPrecioBase());
                        agendaEventoDetalleRepository.save(det9);

                        AgendaEventoEmpleado emp9 = new AgendaEventoEmpleado();
                        emp9.setAgendaEvento(ev9);
                        emp9.setEmpleado(dra);
                        emp9.setRolEnEvento(RolEmpleadoEvento.RESPONSABLE);
                        agendaEventoEmpleadoRepository.save(emp9);

                        // Cita 3 — Max, consulta veterinaria + baño, PENDIENTE (mañana)
                        AgendaEvento ev10 = new AgendaEvento();
                        ev10.setCliente(roberto);
                        ev10.setMascota(max);
                        ev10.setSucursal(sucVet);
                        ev10.setCreadoPorUsuario(recepVet);
                        ev10.setTipoEvento(TipoEvento.CITA);
                        ev10.setEstado(EstadoEvento.PENDIENTE);
                        ev10.setInicio(OffsetDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0));
                        ev10.setFin(OffsetDateTime.now().plusDays(1).withHour(10).withMinute(40).withSecond(0).withNano(0));
                        ev10.setNotas("Revisión general + baño. Verificar zona de la oreja izquierda.");
                        agendaEventoRepository.save(ev10);

                        AgendaEventoDetalle det10a = new AgendaEventoDetalle();
                        det10a.setAgendaEvento(ev10);
                        det10a.setServicio(consulta);
                        det10a.setDuracionEstimadaMinutos(consulta.getDuracionMinutos());
                        det10a.setPrecioAcordado(consulta.getPrecioBase());
                        agendaEventoDetalleRepository.save(det10a);

                        AgendaEventoDetalle det10b = new AgendaEventoDetalle();
                        det10b.setAgendaEvento(ev10);
                        det10b.setServicio(banoHipo);
                        det10b.setDuracionEstimadaMinutos(banoHipo.getDuracionMinutos());
                        det10b.setPrecioAcordado(banoHipo.getPrecioBase());
                        agendaEventoDetalleRepository.save(det10b);

                        AgendaEventoEmpleado emp10 = new AgendaEventoEmpleado();
                        emp10.setAgendaEvento(ev10);
                        emp10.setEmpleado(dra);
                        emp10.setRolEnEvento(RolEmpleadoEvento.RESPONSABLE);
                        agendaEventoEmpleadoRepository.save(emp10);

                        log.info("Citas VETERINARIA 'Klipp Pet Grooming' creadas");
                    }
                });
    }

}
