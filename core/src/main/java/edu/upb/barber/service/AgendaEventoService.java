package edu.upb.barber.service;

import edu.upb.barber.repository.*;
import edu.upb.barber.repository.dto.request.AgendaEventoCreateRequestDto;
import edu.upb.barber.repository.dto.request.AgendaEventoDetalleCreateDto;
import edu.upb.barber.repository.dto.request.AgendaEventoEmpleadoCreateDto;
import edu.upb.barber.repository.dto.response.AgendaEventoCreateResponseDto;
import edu.upb.barber.repository.dto.response.AgendaEventoResponseDto;
import edu.upb.barber.repository.entity.*;
import edu.upb.barber.repository.entity.enums.EstadoEvento;
import edu.upb.barber.repository.entity.enums.RolEmpleadoEvento;
import edu.upb.barber.repository.entity.enums.TipoEvento;
import edu.upb.barber.repository.dto.request.WalkInRequestDto;
import edu.upb.barber.repository.dto.request.VentaRequestDto;
import edu.upb.barber.repository.dto.request.VentaDetalleRequestDto;
import edu.upb.barber.repository.entity.enums.TipoItemVenta;
import edu.upb.barber.repository.entity.enums.MetodoPago;
import edu.upb.barber.repository.entity.enums.EstadoPago;
import edu.upb.barber.repository.dto.request.PagoRequestDto;
import edu.upb.barber.repository.dto.response.VentaResponseDto;
import edu.upb.barber.service.exception.OperationException;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class AgendaEventoService {

    private final AgendaEventoRepository agendaEventoRepository;
    private final AgendaEventoDetalleRepository agendaEventoDetalleRepository;
    private final AgendaEventoEmpleadoRepository agendaEventoEmpleadoRepository;
    private final SucursalRepository sucursalRepository;
    private final ClienteRepository clienteRepository;
    private final MascotaRepository mascotaRepository;
    private final ServicioRepository servicioRepository;
    private final ComboServicioRepository comboServicioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final EmpleadoSucursalRepository empleadoSucursalRepository;
    private final LogService logService;
    private final VentaService ventaService;
    private final PagoService pagoService;
    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final EmailService emailService;

    @Transactional
    public AgendaEventoCreateResponseDto crear(AgendaEventoCreateRequestDto request) throws Exception {

        validarRequestBase(request);

        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new OperationException("Sucursal no encontrada con ID: " + request.getSucursalId()));

        Cliente cliente = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario principal = null;
        if (authentication != null && authentication.getPrincipal() instanceof Usuario) {
            principal = (Usuario) authentication.getPrincipal();
        }

        if (principal != null && principal.getRol() == edu.upb.barber.repository.entity.enums.RolUsuario.ROLE_CLIENTE) {
            final String usuarioId = principal.getId();
            final String empresaId = sucursal.getEmpresa().getId();
            
            cliente = clienteRepository.findByUsuarioIdAndEmpresaId(usuarioId, empresaId)
                    .orElse(null);
            
            if (cliente == null) {
                // Copiar datos de contacto de otro perfil de cliente si existe
                List<Cliente> clientesExistentes = clienteRepository.findByUsuarioId(usuarioId);
                String telefono = "";
                String documento = "";
                if (!clientesExistentes.isEmpty()) {
                    Cliente primerCliente = clientesExistentes.get(0);
                    telefono = primerCliente.getTelefono();
                    documento = primerCliente.getDocumento();
                }
                
                cliente = new Cliente();
                String fullName = principal.getNombre();
                if (principal.getApellido() != null && !principal.getApellido().isBlank()) {
                    fullName += " " + principal.getApellido();
                }
                cliente.setNombre(fullName);
                cliente.setTelefono(telefono);
                cliente.setDocumento(documento);
                cliente.setEmail(principal.getEmail());
                cliente.setUsuario(principal);
                cliente.setEmpresa(sucursal.getEmpresa());
                cliente.setActivo(true);
                
                cliente = clienteRepository.save(cliente);
                log.info("Cliente creado automáticamente para usuario {} en empresa {}", principal.getEmail(), sucursal.getEmpresa().getNombre());
            }
        } else if (request.getClienteId() != null && !request.getClienteId().isBlank()) {
            cliente = clienteRepository.findById(request.getClienteId())
                    .orElseThrow(() -> new OperationException("Cliente no encontrado con ID: " + request.getClienteId()));
        }

        // Si el cliente es null y hay un usuario autenticado con Rol Cliente, resolver o crear su perfil de Cliente para esta empresa
        if (cliente == null) {
            Usuario currentUser = null;
            if (org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication() != null &&
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Usuario) {
                currentUser = (Usuario) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            }

            if (currentUser != null && currentUser.getRol() == edu.upb.barber.repository.entity.enums.RolUsuario.ROLE_CLIENTE) {
                // Buscar si ya tiene un perfil de Cliente para la Empresa de la sucursal actual
                Optional<Cliente> clienteExistente = clienteRepository.findByUsuarioIdAndEmpresaId(currentUser.getId(), sucursal.getEmpresa().getId());
                if (clienteExistente.isPresent()) {
                    cliente = clienteExistente.get();
                } else {
                    // Si no tiene perfil de Cliente en esta empresa, lo creamos ahora asociado a la Empresa de la sucursal de la cita
                    cliente = new Cliente();
                    cliente.setNombre(currentUser.getNombre() + (currentUser.getApellido() != null ? " " + currentUser.getApellido() : ""));
                    cliente.setEmail(currentUser.getEmail());
                    cliente.setTelefono(currentUser.getTelefono());
                    cliente.setDocumento(currentUser.getDocumento());
                    cliente.setUsuario(currentUser);
                    cliente.setEmpresa(sucursal.getEmpresa());
                    cliente.setActivo(true);
                    cliente = clienteRepository.save(cliente);
                    log.info("Perfil de Cliente creado automáticamente para el usuario: {} en la empresa: {}", currentUser.getEmail(), sucursal.getEmpresa().getId());
                }
            }
        }

        Mascota mascota = null;
        if (request.getMascotaId() != null && !request.getMascotaId().isBlank()) {
            mascota = mascotaRepository.findById(request.getMascotaId())
                    .orElseThrow(() -> new OperationException("Mascota no encontrada con ID: " + request.getMascotaId()));
        }

        validarReglasPorTipo(request);
        validarAsignaciones(request.getEmpleados(), request.getSucursalId(), request);
        validarDetalles(request.getDetalles(), request.getTipoEvento());

        AgendaEvento agendaEvento = new AgendaEvento();
        agendaEvento.setSucursal(sucursal);
        agendaEvento.setCliente(cliente);
        agendaEvento.setMascota(mascota);
        agendaEvento.setTipoEvento(request.getTipoEvento());
        agendaEvento.setEstado(EstadoEvento.PENDIENTE);
        agendaEvento.setInicio(request.getInicio());
        agendaEvento.setFin(request.getFin());
        agendaEvento.setNotas(request.getNotas());

        agendaEvento = agendaEventoRepository.save(agendaEvento);

        if (request.getDetalles() != null) {
            for (AgendaEventoDetalleCreateDto detalleDto : request.getDetalles()) {
                AgendaEventoDetalle detalle = new AgendaEventoDetalle();
                detalle.setAgendaEvento(agendaEvento);
                detalle.setDuracionEstimadaMinutos(detalleDto.getDuracionEstimadaMinutos() != null ? detalleDto.getDuracionEstimadaMinutos() : 0);
                detalle.setPrecioAcordado(detalleDto.getPrecioAcordado());
                detalle.setNotas(detalleDto.getNotas());

                if (detalleDto.getServicioId() != null && !detalleDto.getServicioId().isBlank()) {
                    Servicio servicio = servicioRepository.findById(detalleDto.getServicioId())
                            .orElseThrow(() -> new OperationException("Servicio no encontrado con ID: " + detalleDto.getServicioId()));
                    detalle.setServicio(servicio);
                } else if (detalleDto.getComboServicioId() != null && !detalleDto.getComboServicioId().isBlank()) {
                    ComboServicio combo = comboServicioRepository.findById(detalleDto.getComboServicioId())
                            .orElseThrow(() -> new OperationException("Combo no encontrado con ID: " + detalleDto.getComboServicioId()));
                    detalle.setComboServicio(combo);
                } else if (detalleDto.getProductoId() != null && !detalleDto.getProductoId().isBlank()) {
                    Producto producto = productoRepository.findById(detalleDto.getProductoId())
                            .orElseThrow(() -> new OperationException("Producto no encontrado con ID: " + detalleDto.getProductoId()));
                    detalle.setProducto(producto);
                    detalle.setCantidad(detalleDto.getCantidad() != null ? detalleDto.getCantidad() : 1);
                    detalle.setDuracionEstimadaMinutos(0);
                }
                agendaEventoDetalleRepository.save(detalle);
            }
        }

        if (request.getEmpleados() != null) {
            for (AgendaEventoEmpleadoCreateDto empleadoDto : request.getEmpleados()) {
                Empleado empleado = empleadoRepository.findById(empleadoDto.getEmpleadoId())
                        .orElseThrow(() -> new OperationException("Empleado no encontrado con ID: " + empleadoDto.getEmpleadoId()));

                AgendaEventoEmpleado agendaEventoEmpleado = new AgendaEventoEmpleado();
                agendaEventoEmpleado.setAgendaEvento(agendaEvento);
                agendaEventoEmpleado.setEmpleado(empleado);
                agendaEventoEmpleado.setRolEnEvento(
                        empleadoDto.getRolEnEvento() == null ? RolEmpleadoEvento.RESPONSABLE : empleadoDto.getRolEnEvento()
                );
                agendaEventoEmpleadoRepository.save(agendaEventoEmpleado);
            }
        }

        // Enviar email de confirmación
        if (cliente != null && cliente.getEmail() != null && !cliente.getEmail().isBlank()) {
            try {
                final String toEmail = cliente.getEmail().trim();
                
                List<String> serviceNames = new ArrayList<>();
                BigDecimal totalSum = BigDecimal.ZERO;
                if (request.getDetalles() != null) {
                    for (AgendaEventoDetalleCreateDto detalleDto : request.getDetalles()) {
                        if (detalleDto.getServicioId() != null && !detalleDto.getServicioId().isBlank()) {
                            Optional<Servicio> sOpt = servicioRepository.findById(detalleDto.getServicioId());
                            if (sOpt.isPresent()) {
                                serviceNames.add(sOpt.get().getNombre());
                            }
                        } else if (detalleDto.getComboServicioId() != null && !detalleDto.getComboServicioId().isBlank()) {
                            Optional<ComboServicio> cOpt = comboServicioRepository.findById(detalleDto.getComboServicioId());
                            if (cOpt.isPresent()) {
                                serviceNames.add(cOpt.get().getNombre());
                            }
                        }
                        if (detalleDto.getPrecioAcordado() != null) {
                            totalSum = totalSum.add(detalleDto.getPrecioAcordado());
                        }
                    }
                }
                String servicioNombre = String.join(", ", serviceNames);
                if (servicioNombre.isEmpty()) {
                    servicioNombre = "Servicios Varios";
                }

                List<String> employeeNames = new ArrayList<>();
                if (request.getEmpleados() != null) {
                    for (AgendaEventoEmpleadoCreateDto empDto : request.getEmpleados()) {
                        Optional<Empleado> eOpt = empleadoRepository.findById(empDto.getEmpleadoId());
                        if (eOpt.isPresent()) {
                            employeeNames.add(eOpt.get().getNombre());
                        }
                    }
                }
                String empleadoNombre = employeeNames.isEmpty() ? "Asignado automáticamente" : String.join(", ", employeeNames);

                java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", new java.util.Locale("es", "BO"));
                String citaFecha = request.getInicio().format(dateFormatter);
                if (citaFecha.length() > 0) {
                    citaFecha = Character.toUpperCase(citaFecha.charAt(0)) + citaFecha.substring(1);
                }

                java.time.format.DateTimeFormatter timeFormatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm");
                String citaHora = request.getInicio().format(timeFormatter) + " hs";

                String precioTotal = totalSum.setScale(2, java.math.RoundingMode.HALF_UP).toString() + " Bs.";

                String clienteNombre = cliente.getNombre();

                emailService.sendCitaConfirmada(
                        toEmail,
                        clienteNombre,
                        servicioNombre,
                        empleadoNombre,
                        citaFecha,
                        citaHora,
                        sucursal.getNombre(),
                        precioTotal
                );
            } catch (Exception e) {
                log.error("Error al enviar email de confirmacion de cita para evento " + agendaEvento.getId(), e);
            }
        }

        logService.info("AgendaEvento creado exitosamente: " + agendaEvento.getId());
        AgendaEventoCreateResponseDto response = new AgendaEventoCreateResponseDto();
        response.setAgendaEventoId(agendaEvento.getId());
        response.setEstado(agendaEvento.getEstado());
        return response;
    }

    private void validarRequestBase(AgendaEventoCreateRequestDto request) throws Exception {
        if (request == null) {
            logService.error("Error en AgendaEvento. Request no puede ser null");
            throw new OperationException("Request no puede ser null");
        }
        if (request.getSucursalId() == null || request.getSucursalId().isBlank()) {
            log.error("Error en AgendaEvento. sucursal_id es requerido");
            logService.error("Error en AgendaEvento. sucursal_id es requerido");
            throw new OperationException("sucursal_id es requerido");
        }
        if (request.getTipoEvento() == null) {
            log.error("Error en AgendaEvento. tipo_evento es requerido");
            logService.error("Error en AgendaEvento. tipo_evento es requerido");
            throw new OperationException("tipo_evento es requerido");
        }
        if (request.getInicio() == null || request.getFin() == null) {
            log.error("Error en AgendaEvento. inicio y fin son requeridos");
            logService.error("Error en AgendaEvento. inicio y fin son requeridos");
            throw new OperationException("inicio y fin son requeridos");
        }
        if (!request.getInicio().isBefore(request.getFin())) {
            log.error("Error en AgendaEvento. inicio debe ser menor que fin");
            logService.error("Error en AgendaEvento. inicio debe ser menor que fin");
            throw new OperationException("inicio debe ser menor que fin");
        }
    }

    private void validarReglasPorTipo(AgendaEventoCreateRequestDto request) throws Exception {
        if (request.getTipoEvento() == TipoEvento.CITA) {
            if (request.getEmpleados() == null || request.getEmpleados().isEmpty()) {
                log.error("Error en AgendaEvento. Para tipo CITA, empleados es requerido");
                logService.error("Error en AgendaEvento. Para tipo CITA, empleados es requerido");
                throw new OperationException("Para tipo CITA, empleados es requerido");
            }
            if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
                log.error("Error en AgendaEvento. Para tipo CITA, detalles es requerido");
                logService.error("Error en AgendaEvento. Para tipo CITA, detalles es requerido");
                throw new OperationException("Para tipo CITA, detalles es requerido");
            }
        } else if (request.getDetalles() != null && !request.getDetalles().isEmpty()) {
            log.error("Error en AgendaEvento. detalles solo aplica para tipo CITA");
            logService.error("Error en AgendaEvento. detalles solo aplica para tipo CITA");
            throw new OperationException("detalles solo aplica para tipo CITA");
        }
    }

    private void validarDetalles(List<AgendaEventoDetalleCreateDto> detalles, TipoEvento tipoEvento) throws Exception {
        if (detalles == null) {
            return;
        }
        for (AgendaEventoDetalleCreateDto detalle : detalles) {
            boolean tieneServicio = detalle.getServicioId() != null && !detalle.getServicioId().isBlank();
            boolean tieneCombo = detalle.getComboServicioId() != null && !detalle.getComboServicioId().isBlank();
            boolean tieneProducto = detalle.getProductoId() != null && !detalle.getProductoId().isBlank();

            int count = (tieneServicio ? 1 : 0) + (tieneCombo ? 1 : 0) + (tieneProducto ? 1 : 0);
            if (count != 1) {
                log.error("Error en detalle AgendaEvento. Debe tener exactamente uno de: servicio_id, combo_servicio_id o producto_id");
                logService.error("Error en detalle AgendaEvento. Debe tener exactamente uno de: servicio_id, combo_servicio_id o producto_id");
                throw new OperationException("Cada detalle debe tener exactamente uno de: servicio_id, combo_servicio_id o producto_id");
            }
            if (!tieneProducto) {
                if (detalle.getDuracionEstimadaMinutos() == null || detalle.getDuracionEstimadaMinutos() <= 0) {
                    log.error("Error en detalle AgendaEvento. duracion_estimada_minutos debe ser mayor que cero");
                    logService.error("Error en detalle AgendaEvento. duracion_estimada_minutos debe ser mayor que cero");
                    throw new OperationException("duracion_estimada_minutos debe ser mayor que cero");
                }
            }
            if (detalle.getPrecioAcordado() == null || detalle.getPrecioAcordado().compareTo(BigDecimal.ZERO) < 0) {
                log.error("Error en detalle AgendaEvento. precio_acordado debe ser mayor o igual a cero");
                logService.error("Error en detalle AgendaEvento. precio_acordado debe ser mayor o igual a cero");
                throw new OperationException("precio_acordado debe ser mayor o igual a cero");
            }
        }
    }

    private void validarAsignaciones(
            List<AgendaEventoEmpleadoCreateDto> empleados,
            String sucursalId,
            AgendaEventoCreateRequestDto request
    ) throws Exception {
        if (empleados == null || empleados.isEmpty()) {
            return;
        }

        Set<String> ids = new HashSet<>();
        for (AgendaEventoEmpleadoCreateDto asignacion : empleados) {
            if (asignacion.getEmpleadoId() == null || asignacion.getEmpleadoId().isBlank()) {
                log.error("Error en asignacion AgendaEvento. empleado_id es requerido");
                logService.error("Error en asignacion AgendaEvento. empleado_id es requerido en cada asignacion");
                throw new OperationException("empleado_id es requerido en cada asignacion");
            }
            if (!ids.add(asignacion.getEmpleadoId())) {
                log.error("Error en asignacion AgendaEvento. Empleado duplicado: {}", asignacion.getEmpleadoId());
                logService.error("Error en asignacion AgendaEvento. Empleado duplicado: " + asignacion.getEmpleadoId());
                throw new OperationException("Empleado duplicado en asignaciones: " + asignacion.getEmpleadoId());
            }

            Empleado empleado = empleadoRepository.findById(asignacion.getEmpleadoId())
                    .orElseThrow(() -> new OperationException("Empleado no encontrado con ID: " + asignacion.getEmpleadoId()));
            if (!empleado.isActivo()) {
                log.error("Error en asignacion AgendaEvento. Empleado inactivo: {}", empleado.getId());
                logService.error("Error en asignacion AgendaEvento. Empleado inactivo: " + empleado.getId());
                throw new OperationException("Empleado inactivo: " + empleado.getId());
            }

            boolean pertenece = empleadoSucursalRepository
                    .existsByEmpleadoIdAndSucursalIdAndActivoTrue(asignacion.getEmpleadoId(), sucursalId);
            if (!pertenece) {
                log.error("Error en asignacion AgendaEvento. Empleado {} no pertenece a sucursal {}", asignacion.getEmpleadoId(), sucursalId);
                logService.error("Error en asignacion AgendaEvento. Empleado " + asignacion.getEmpleadoId() + " no pertenece a la sucursal " + sucursalId);
                throw new OperationException("Empleado " + asignacion.getEmpleadoId() + " no pertenece a la sucursal " + sucursalId);
            }

            boolean enConflicto = agendaEventoEmpleadoRepository.existsConflictoHorarioEmpleado(
                    asignacion.getEmpleadoId(),
                    request.getInicio(),
                    request.getFin(),
                    Arrays.asList(EstadoEvento.CANCELADO, EstadoEvento.NO_SHOW)
            );
            if (enConflicto) {
                log.error("Error en asignacion AgendaEvento. Conflicto de horario para empleado: {}", asignacion.getEmpleadoId());
                logService.error("Error en asignacion AgendaEvento. Conflicto de horario para empleado: " + asignacion.getEmpleadoId());
                throw new OperationException("Conflicto de horario para empleado: " + asignacion.getEmpleadoId());
            }
        }
    }

    @Transactional(readOnly = true)
    public List<AgendaEventoResponseDto> listar() {
        edu.upb.barber.repository.entity.Usuario currentUser = null;
        if (org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication() != null &&
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof edu.upb.barber.repository.entity.Usuario) {
            currentUser = (edu.upb.barber.repository.entity.Usuario) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }

        List<AgendaEvento> eventos;
        if (currentUser != null && currentUser.getRol() == edu.upb.barber.repository.entity.enums.RolUsuario.ROLE_CLIENTE) {
            List<String> clienteIds = clienteRepository.findByUsuarioId(currentUser.getId()).stream()
                    .map(Cliente::getId)
                    .toList();
            if (clienteIds.isEmpty()) {
                eventos = List.of();
            } else {
                eventos = agendaEventoRepository.findByClienteIdIn(clienteIds);
            }
        } else if (currentUser != null && currentUser.getEmpresa() != null) {
            eventos = agendaEventoRepository.findBySucursal_Empresa(currentUser.getEmpresa());
        } else {
            eventos = agendaEventoRepository.findAll();
        }

        return eventos.stream()
                .map(ae -> {
                    AgendaEventoResponseDto dto = new AgendaEventoResponseDto(ae);
                    List<AgendaEventoResponseDto.DetalleDto> detalles = agendaEventoDetalleRepository.findByAgendaEventoId(ae.getId()).stream()
                            .map(this::mapDetalleToDto) //
                                    /* d.getServicio() != null ? d.getServicio().getId() : null,
                                    d.getServicio() != null ? d.getServicio().getNombre() : "Combo",
                                    d.getPrecioAcordado() != null ? d.getPrecioAcordado().doubleValue() : 0.0,
                                    d.getDuracionEstimadaMinutos()
                            */
                            .toList();
                    dto.setDetalles(detalles);

                    List<AgendaEventoResponseDto.EmpleadoDto> empleados = agendaEventoEmpleadoRepository.findByAgendaEventoId(ae.getId()).stream()
                            .map(e -> new AgendaEventoResponseDto.EmpleadoDto(
                                    e.getEmpleado().getId(),
                                    e.getEmpleado().getNombre(),
                                    e.getRolEnEvento() != null ? e.getRolEnEvento().name() : null
                            ))
                            .toList();
                    dto.setEmpleados(empleados);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<AgendaEventoResponseDto> findById(String id) {
        return agendaEventoRepository.findById(id)
                .map(ae -> {
                    AgendaEventoResponseDto dto = new AgendaEventoResponseDto(ae);
                    List<AgendaEventoResponseDto.DetalleDto> detalles = agendaEventoDetalleRepository.findByAgendaEventoId(ae.getId()).stream()
                            .map(this::mapDetalleToDto) //
                                    /* d.getServicio() != null ? d.getServicio().getId() : null,
                                    d.getServicio() != null ? d.getServicio().getNombre() : "Combo",
                                    d.getPrecioAcordado() != null ? d.getPrecioAcordado().doubleValue() : 0.0,
                                    d.getDuracionEstimadaMinutos()
                            */
                            .toList();
                    dto.setDetalles(detalles);

                    List<AgendaEventoResponseDto.EmpleadoDto> empleados = agendaEventoEmpleadoRepository.findByAgendaEventoId(ae.getId()).stream()
                            .map(e -> new AgendaEventoResponseDto.EmpleadoDto(
                                    e.getEmpleado().getId(),
                                    e.getEmpleado().getNombre(),
                                    e.getRolEnEvento() != null ? e.getRolEnEvento().name() : null
                            ))
                            .toList();
                    dto.setEmpleados(empleados);

                    return dto;
                });
    }

    @Transactional
    public AgendaEventoCreateResponseDto update(String id, AgendaEventoCreateRequestDto request) throws Exception {
        AgendaEvento agendaEvento = agendaEventoRepository.findById(id)
                .orElseThrow(() -> new OperationException("AgendaEvento no encontrado con ID: " + id));

        validarRequestBase(request);

        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new OperationException("Sucursal no encontrada con ID: " + request.getSucursalId()));

        Cliente cliente = null;
        if (request.getClienteId() != null && !request.getClienteId().isBlank()) {
            cliente = clienteRepository.findById(request.getClienteId())
                    .orElseThrow(() -> new OperationException("Cliente no encontrado con ID: " + request.getClienteId()));
        }

        Mascota mascota = null;
        if (request.getMascotaId() != null && !request.getMascotaId().isBlank()) {
            mascota = mascotaRepository.findById(request.getMascotaId())
                    .orElseThrow(() -> new OperationException("Mascota no encontrada con ID: " + request.getMascotaId()));
        }

        validarReglasPorTipo(request);
        validarAsignacionesParaUpdate(request.getEmpleados(), request.getSucursalId(), request, id);
        validarDetalles(request.getDetalles(), request.getTipoEvento());

        agendaEventoDetalleRepository.deleteByAgendaEventoId(id);
        agendaEventoEmpleadoRepository.deleteByAgendaEventoId(id);

        agendaEvento.setSucursal(sucursal);
        agendaEvento.setCliente(cliente);
        agendaEvento.setMascota(mascota);
        agendaEvento.setTipoEvento(request.getTipoEvento());
        agendaEvento.setInicio(request.getInicio());
        agendaEvento.setFin(request.getFin());
        agendaEvento.setNotas(request.getNotas());

        agendaEvento = agendaEventoRepository.save(agendaEvento);

        if (request.getDetalles() != null) {
            for (AgendaEventoDetalleCreateDto detalleDto : request.getDetalles()) {
                AgendaEventoDetalle detalle = new AgendaEventoDetalle();
                detalle.setAgendaEvento(agendaEvento);
                detalle.setDuracionEstimadaMinutos(detalleDto.getDuracionEstimadaMinutos() != null ? detalleDto.getDuracionEstimadaMinutos() : 0);
                detalle.setPrecioAcordado(detalleDto.getPrecioAcordado());
                detalle.setNotas(detalleDto.getNotas());

                if (detalleDto.getServicioId() != null && !detalleDto.getServicioId().isBlank()) {
                    Servicio servicio = servicioRepository.findById(detalleDto.getServicioId())
                            .orElseThrow(() -> new OperationException("Servicio no encontrado con ID: " + detalleDto.getServicioId()));
                    detalle.setServicio(servicio);
                } else if (detalleDto.getComboServicioId() != null && !detalleDto.getComboServicioId().isBlank()) {
                    ComboServicio combo = comboServicioRepository.findById(detalleDto.getComboServicioId())
                            .orElseThrow(() -> new OperationException("Combo no encontrado con ID: " + detalleDto.getComboServicioId()));
                    detalle.setComboServicio(combo);
                } else if (detalleDto.getProductoId() != null && !detalleDto.getProductoId().isBlank()) {
                    Producto producto = productoRepository.findById(detalleDto.getProductoId())
                            .orElseThrow(() -> new OperationException("Producto no encontrado con ID: " + detalleDto.getProductoId()));
                    detalle.setProducto(producto);
                    detalle.setCantidad(detalleDto.getCantidad() != null ? detalleDto.getCantidad() : 1);
                    detalle.setDuracionEstimadaMinutos(0);
                }
                agendaEventoDetalleRepository.save(detalle);
            }
        }

        if (request.getEmpleados() != null) {
            for (AgendaEventoEmpleadoCreateDto empleadoDto : request.getEmpleados()) {
                Empleado empleado = empleadoRepository.findById(empleadoDto.getEmpleadoId())
                        .orElseThrow(() -> new OperationException("Empleado no encontrado con ID: " + empleadoDto.getEmpleadoId()));

                AgendaEventoEmpleado agendaEventoEmpleado = new AgendaEventoEmpleado();
                agendaEventoEmpleado.setAgendaEvento(agendaEvento);
                agendaEventoEmpleado.setEmpleado(empleado);
                agendaEventoEmpleado.setRolEnEvento(
                        empleadoDto.getRolEnEvento() == null ? RolEmpleadoEvento.RESPONSABLE : empleadoDto.getRolEnEvento()
                );
                agendaEventoEmpleadoRepository.save(agendaEventoEmpleado);
            }
        }

        logService.info("AgendaEvento actualizado exitosamente: " + id);
        AgendaEventoCreateResponseDto response = new AgendaEventoCreateResponseDto();
        response.setAgendaEventoId(agendaEvento.getId());
        response.setEstado(agendaEvento.getEstado());
        return response;
    }

    @Transactional
    public void delete(String id) throws Exception {
        AgendaEvento agendaEvento = agendaEventoRepository.findById(id)
                .orElseThrow(() -> new OperationException("AgendaEvento no encontrado con ID: " + id));

        agendaEventoDetalleRepository.deleteByAgendaEventoId(id);
        agendaEventoEmpleadoRepository.deleteByAgendaEventoId(id);
        agendaEventoRepository.delete(agendaEvento);
        logService.info("AgendaEvento eliminado exitosamente: " + id);
    }

    @Transactional
    public AgendaEventoResponseDto actualizarEstado(String id, EstadoEvento nuevoEstado) throws Exception {
        AgendaEvento agendaEvento = agendaEventoRepository.findById(id)
                .orElseThrow(() -> new OperationException("AgendaEvento no encontrado con ID: " + id));
        
        EstadoEvento estadoAnterior = agendaEvento.getEstado();
        agendaEvento.setEstado(nuevoEstado);
        agendaEvento = agendaEventoRepository.save(agendaEvento);
        logService.info("Estado de AgendaEvento actualizado a " + nuevoEstado + " para id: " + id);
        
        if (nuevoEstado == EstadoEvento.CONFIRMADO && estadoAnterior != EstadoEvento.CONFIRMADO) {
            enviarEmailConfirmacion(agendaEvento);
        }
        
        return new AgendaEventoResponseDto(agendaEvento);
    }

    private void enviarEmailConfirmacion(AgendaEvento agendaEvento) {
        try {
            if (agendaEvento.getCliente() != null && agendaEvento.getCliente().getEmail() != null && !agendaEvento.getCliente().getEmail().isBlank()) {
                List<AgendaEventoDetalle> detalles = agendaEventoDetalleRepository.findByAgendaEventoId(agendaEvento.getId());
                List<AgendaEventoEmpleado> empleados = agendaEventoEmpleadoRepository.findByAgendaEventoId(agendaEvento.getId());

                String clienteNombre = agendaEvento.getCliente().getNombre();
                
                String servicioNombre = detalles.stream()
                        .map(d -> d.getServicio() != null ? d.getServicio().getNombre() : (d.getComboServicio() != null ? d.getComboServicio().getNombre() : (d.getProducto() != null ? d.getProducto().getNombre() : "")))
                        .filter(name -> name != null && !name.isBlank())
                        .collect(Collectors.joining(", "));

                String empleadoNombre = empleados.stream()
                        .map(e -> e.getEmpleado() != null ? e.getEmpleado().getNombre() : "")
                        .filter(name -> name != null && !name.isBlank())
                        .collect(Collectors.joining(", "));

                String citaFecha = "";
                String citaHora = "";
                if (agendaEvento.getInicio() != null) {
                    java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("EEEE dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
                    java.time.format.DateTimeFormatter timeFormatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm");
                    citaFecha = agendaEvento.getInicio().format(dateFormatter);
                    citaHora = agendaEvento.getInicio().format(timeFormatter);
                }

                String sucursalNombre = agendaEvento.getSucursal() != null ? agendaEvento.getSucursal().getNombre() : "";

                BigDecimal total = detalles.stream()
                        .map(d -> d.getPrecioAcordado() != null ? d.getPrecioAcordado() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                String precioTotal = total.toString() + " Bs.";

                emailService.sendConfirmationEmail(
                        agendaEvento.getCliente().getEmail(),
                        clienteNombre,
                        servicioNombre,
                        empleadoNombre,
                        citaFecha,
                        citaHora,
                        sucursalNombre,
                        precioTotal
                );
            }
        } catch (Exception ex) {
            log.error("Error al enviar email de confirmación de cita para id: {}", agendaEvento.getId(), ex);
        }
    }

    private void validarAsignacionesParaUpdate(
            List<AgendaEventoEmpleadoCreateDto> empleados,
            String sucursalId,
            AgendaEventoCreateRequestDto request,
            String excludeEventoId
    ) throws Exception {
        if (empleados == null || empleados.isEmpty()) {
            return;
        }

        Set<String> ids = new HashSet<>();
        for (AgendaEventoEmpleadoCreateDto asignacion : empleados) {
            if (asignacion.getEmpleadoId() == null || asignacion.getEmpleadoId().isBlank()) {
                log.error("Error en asignacion AgendaEvento. empleado_id es requerido");
                logService.error("Error en asignacion AgendaEvento. empleado_id es requerido en cada asignacion");
                throw new OperationException("empleado_id es requerido en cada asignacion");
            }
            if (!ids.add(asignacion.getEmpleadoId())) {
                log.error("Error en asignacion AgendaEvento. Empleado duplicado: {}", asignacion.getEmpleadoId());
                logService.error("Error en asignacion AgendaEvento. Empleado duplicado: " + asignacion.getEmpleadoId());
                throw new OperationException("Empleado duplicado en asignaciones: " + asignacion.getEmpleadoId());
            }

            Empleado empleado = empleadoRepository.findById(asignacion.getEmpleadoId())
                    .orElseThrow(() -> new OperationException("Empleado no encontrado con ID: " + asignacion.getEmpleadoId()));
            if (!empleado.isActivo()) {
                log.error("Error en asignacion AgendaEvento. Empleado inactivo: {}", empleado.getId());
                logService.error("Error en asignacion AgendaEvento. Empleado inactivo: " + empleado.getId());
                throw new OperationException("Empleado inactivo: " + empleado.getId());
            }

            boolean pertenece = empleadoSucursalRepository
                    .existsByEmpleadoIdAndSucursalIdAndActivoTrue(asignacion.getEmpleadoId(), sucursalId);
            if (!pertenece) {
                log.error("Error en asignacion AgendaEvento. Empleado {} no pertenece a sucursal {}", asignacion.getEmpleadoId(), sucursalId);
                logService.error("Error en asignacion AgendaEvento. Empleado " + asignacion.getEmpleadoId() + " no pertenece a la sucursal " + sucursalId);
                throw new OperationException("Empleado " + asignacion.getEmpleadoId() + " no pertenece a la sucursal " + sucursalId);
            }

            boolean enConflicto = agendaEventoEmpleadoRepository.existsConflictoHorarioEmpleadoExcludingEvent(
                    asignacion.getEmpleadoId(),
                    excludeEventoId,
                    request.getInicio(),
                    request.getFin(),
                    Arrays.asList(EstadoEvento.CANCELADO, EstadoEvento.NO_SHOW)
            );
            if (enConflicto) {
                log.error("Error en asignacion AgendaEvento. Conflicto de horario para empleado: {}", asignacion.getEmpleadoId());
                logService.error("Error en asignacion AgendaEvento. Conflicto de horario para empleado: " + asignacion.getEmpleadoId());
                throw new OperationException("Conflicto de horario para empleado: " + asignacion.getEmpleadoId());
            }
        }
    }

    @Transactional
    public void registrarWalkIn(WalkInRequestDto request) throws Exception {
        if (request.empleadoId() == null || request.empleadoId().isBlank()) {
            throw new OperationException("El campo empleado_id es requerido");
        }
        if (request.clienteId() == null || request.clienteId().isBlank()) {
            throw new OperationException("El campo cliente_id es requerido");
        }
        if (request.servicioIds() == null || request.servicioIds().isEmpty()) {
            throw new OperationException("Debe seleccionar al menos un servicio para el walk-in");
        }

        // 1. Buscar Empleado
        Empleado empleado = empleadoRepository.findById(request.empleadoId())
                .orElseThrow(() -> new OperationException("Empleado no encontrado con ID: " + request.empleadoId()));

        if (!empleado.isActivo()) {
            throw new OperationException("El empleado seleccionado no está activo");
        }

        // 2. Determinar Sucursal activa del empleado
        List<EmpleadoSucursal> asignaciones = empleadoSucursalRepository.findByEmpleadoIdAndActivoTrue(empleado.getId());
        if (asignaciones.isEmpty()) {
            throw new OperationException("El empleado no tiene asignada ninguna sucursal activa");
        }
        Sucursal sucursal = asignaciones.get(0).getSucursal();

        // 3. Buscar Cliente
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new OperationException("Cliente no encontrado con ID: " + request.clienteId()));

        // 4. Calcular duración y preparar servicios
        int duracionTotal = 0;
        List<Servicio> servicios = new ArrayList<>();
        for (String servicioId : request.servicioIds()) {
            Servicio servicio = servicioRepository.findById(servicioId)
                    .orElseThrow(() -> new OperationException("Servicio no encontrado con ID: " + servicioId));
            servicios.add(servicio);
            duracionTotal += servicio.getDuracionMinutos();
        }

        OffsetDateTime inicio = OffsetDateTime.now();
        OffsetDateTime fin = inicio.plusMinutes(duracionTotal);

        // 5. Crear e insertar el AgendaEvento
        AgendaEvento agendaEvento = new AgendaEvento();
        agendaEvento.setSucursal(sucursal);
        agendaEvento.setCliente(cliente);
        agendaEvento.setTipoEvento(TipoEvento.CITA);
        agendaEvento.setEstado(EstadoEvento.FINALIZADO); // Walk-in es atención al instante y finalizada
        agendaEvento.setInicio(inicio);
        agendaEvento.setFin(fin);
        agendaEvento.setNotas("Atención rápida (Walk-in)");
        
        agendaEvento = agendaEventoRepository.save(agendaEvento);

        // 6. Crear los detalles de los servicios en la cita
        for (Servicio servicio : servicios) {
            AgendaEventoDetalle detalle = new AgendaEventoDetalle();
            detalle.setAgendaEvento(agendaEvento);
            detalle.setServicio(servicio);
            detalle.setDuracionEstimadaMinutos(servicio.getDuracionMinutos());
            detalle.setPrecioAcordado(servicio.getPrecioBase());
            detalle.setNotas("Servicio Walk-in");
            agendaEventoDetalleRepository.save(detalle);
        }

        // 7. Asignar el empleado responsable de la cita
        AgendaEventoEmpleado asignacionEmpleado = new AgendaEventoEmpleado();
        asignacionEmpleado.setAgendaEvento(agendaEvento);
        asignacionEmpleado.setEmpleado(empleado);
        asignacionEmpleado.setRolEnEvento(RolEmpleadoEvento.RESPONSABLE);
        agendaEventoEmpleadoRepository.save(asignacionEmpleado);

        // 8. Crear la boleta de venta (Venta)
        List<VentaDetalleRequestDto> detallesVenta = new ArrayList<>();
        BigDecimal totalVenta = BigDecimal.ZERO;

        // Agregar detalles de los servicios prestados a la venta
        for (Servicio servicio : servicios) {
            VentaDetalleRequestDto detDto = new VentaDetalleRequestDto();
            detDto.setTipoItem(TipoItemVenta.SERVICIO);
            detDto.setServicioId(servicio.getId());
            detDto.setEmpleadoId(empleado.getId());
            detDto.setCantidad(1);
            detDto.setPrecioUnitario(servicio.getPrecioBase() != null ? servicio.getPrecioBase() : BigDecimal.ZERO);
            detDto.setDescuento(BigDecimal.ZERO);
            detDto.setNotas("Servicio Walk-in");
            detallesVenta.add(detDto);
            totalVenta = totalVenta.add(detDto.getPrecioUnitario());
        }

        // Agregar detalles de los productos vendidos con cantidad (si los hay)
        if (request.productos() != null) {
            for (WalkInRequestDto.ProductoCantidadDto productoDto : request.productos()) {
                if (productoDto == null || productoDto.productoId() == null || productoDto.productoId().isBlank()) {
                    continue;
                }
                int cantidad = productoDto.cantidad() != null ? productoDto.cantidad() : 0;
                if (cantidad <= 0) {
                    continue;
                }

                Producto producto = productoRepository.findById(productoDto.productoId())
                        .orElseThrow(() -> new OperationException("Producto no encontrado con ID: " + productoDto.productoId()));

                BigDecimal precioUnitario = producto.getPrecioVenta() != null ? producto.getPrecioVenta() : BigDecimal.ZERO;
                VentaDetalleRequestDto detDto = new VentaDetalleRequestDto();
                detDto.setTipoItem(TipoItemVenta.PRODUCTO);
                detDto.setProductoId(producto.getId());
                detDto.setEmpleadoId(empleado.getId());
                detDto.setCantidad(cantidad);
                detDto.setPrecioUnitario(precioUnitario);
                detDto.setDescuento(BigDecimal.ZERO);
                detDto.setNotas("Producto vendido en Walk-in");
                detallesVenta.add(detDto);
                totalVenta = totalVenta.add(precioUnitario.multiply(BigDecimal.valueOf(cantidad)));
            }
        } else if (request.productoIds() != null) {
            for (String productoId : request.productoIds()) {
                if (productoId == null || productoId.isBlank()) continue;
                Producto producto = productoRepository.findById(productoId)
                        .orElseThrow(() -> new OperationException("Producto no encontrado con ID: " + productoId));

                BigDecimal precioUnitario = producto.getPrecioVenta() != null ? producto.getPrecioVenta() : BigDecimal.ZERO;
                VentaDetalleRequestDto detDto = new VentaDetalleRequestDto();
                detDto.setTipoItem(TipoItemVenta.PRODUCTO);
                detDto.setProductoId(producto.getId());
                detDto.setEmpleadoId(empleado.getId());
                detDto.setCantidad(1);
                detDto.setPrecioUnitario(precioUnitario);
                detDto.setDescuento(BigDecimal.ZERO);
                detDto.setNotas("Producto vendido en Walk-in");
                detallesVenta.add(detDto);
                totalVenta = totalVenta.add(precioUnitario);
            }
        }

        // Construir la petición de venta
        VentaRequestDto ventaRequest = new VentaRequestDto();
        ventaRequest.setSucursalId(sucursal.getId());
        ventaRequest.setClienteId(cliente.getId());
        ventaRequest.setSubtotal(totalVenta);
        ventaRequest.setDescuento(BigDecimal.ZERO);
        ventaRequest.setTotal(totalVenta);
        ventaRequest.setNotas("Venta automática generada por Walk-in");
        ventaRequest.setDetalles(detallesVenta);

        ventaRequest.setAgendaEventoId(agendaEvento.getId());

        // Crear venta
        VentaResponseDto ventaDto = ventaService.crear(ventaRequest);

        // Crear el Pago como PAGADO (esto gestiona internamente la reducción de stock y marca la venta como COBRADA)
        PagoRequestDto pagoRequest = new PagoRequestDto();
        pagoRequest.setVentaId(ventaDto.getId());
        pagoRequest.setMonto(ventaDto.getTotal());
        pagoRequest.setMetodoPago(MetodoPago.EFECTIVO);
        pagoRequest.setEstadoPago(EstadoPago.PAGADO);
        pagoRequest.setPagadoEn(OffsetDateTime.now());

        pagoService.crear(pagoRequest);

        logService.info("Walk-in registrado con éxito. Cita ID: " + agendaEvento.getId());
    }

    private AgendaEventoResponseDto.DetalleDto mapDetalleToDto(AgendaEventoDetalle d) {
        String tipo = "SERVICIO";
        String sId = null;
        String sNombre = null;
        if (d.getServicio() != null) {
            sId = d.getServicio().getId();
            sNombre = d.getServicio().getNombre();
            tipo = "SERVICIO";
        } else if (d.getComboServicio() != null) {
            sId = d.getComboServicio().getId();
            sNombre = d.getComboServicio().getNombre();
            tipo = "COMBO";
        } else if (d.getProducto() != null) {
            sNombre = d.getProducto().getNombre() + " (Producto)";
            tipo = "PRODUCTO";
        }
        return new AgendaEventoResponseDto.DetalleDto(
                sId,
                sNombre,
                d.getPrecioAcordado() != null ? d.getPrecioAcordado().doubleValue() : 0.0,
                d.getDuracionEstimadaMinutos(),
                d.getProducto() != null ? d.getProducto().getId() : null,
                d.getProducto() != null ? d.getProducto().getNombre() : null,
                d.getCantidad(),
                tipo
        );
    }

    @Transactional(readOnly = true)
    public List<Map<String, String>> obtenerIntervalosOcupados(String empleadoId, java.time.LocalDate fecha) {
        java.time.OffsetDateTime inicioDia = fecha.atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toOffsetDateTime();
        java.time.OffsetDateTime finDia = fecha.plusDays(1).atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toOffsetDateTime();

        List<AgendaEventoEmpleado> asignaciones = agendaEventoEmpleadoRepository.findByEmpleadoId(empleadoId);
        List<Map<String, String>> ocupados = new ArrayList<>();

        for (AgendaEventoEmpleado aee : asignaciones) {
            AgendaEvento ae = aee.getAgendaEvento();
            if (ae.getEstado() != EstadoEvento.CANCELADO && ae.getEstado() != EstadoEvento.NO_SHOW) {
                if (ae.getInicio().isBefore(finDia) && ae.getFin().isAfter(inicioDia)) {
                    Map<String, String> intervalo = new HashMap<>();
                    intervalo.put("inicio", ae.getInicio().toString());
                    intervalo.put("fin", ae.getFin().toString());
                    ocupados.add(intervalo);
                }
            }
        }
        return ocupados;
    }

}
