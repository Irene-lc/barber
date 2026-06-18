package edu.upb.barber.service;

import edu.upb.barber.repository.*;
import edu.upb.barber.repository.dto.request.AgendaEventoCreateRequestDto;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
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
import edu.upb.barber.service.exception.OperationException;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ProductoRepository productoRepository;
    private final EmailService emailService;

    @Transactional
    public AgendaEventoCreateResponseDto crear(AgendaEventoCreateRequestDto request) throws Exception {

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
                detalle.setDuracionEstimadaMinutos(detalleDto.getDuracionEstimadaMinutos());
                detalle.setPrecioAcordado(detalleDto.getPrecioAcordado());
                detalle.setNotas(detalleDto.getNotas());

                if (detalleDto.getServicioId() != null && !detalleDto.getServicioId().isBlank()) {
                    Servicio servicio = servicioRepository.findById(detalleDto.getServicioId())
                            .orElseThrow(() -> new OperationException("Servicio no encontrado con ID: " + detalleDto.getServicioId()));
                    detalle.setServicio(servicio);
                } else {
                    ComboServicio combo = comboServicioRepository.findById(detalleDto.getComboServicioId())
                            .orElseThrow(() -> new OperationException("Combo no encontrado con ID: " + detalleDto.getComboServicioId()));
                    detalle.setComboServicio(combo);
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

        logService.info("AgendaEvento creado exitosamente: " + agendaEvento.getId());

        // Enviar confirmación al cliente si tiene email
        if (cliente != null && cliente.getEmail() != null && !cliente.getEmail().isBlank()
                && agendaEvento.getTipoEvento() == TipoEvento.CITA) {
            try {
                DateTimeFormatter fechaFmt = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM yyyy", new Locale("es", "ES"));
                DateTimeFormatter horaFmt = DateTimeFormatter.ofPattern("HH:mm");
                String fecha = agendaEvento.getInicio().atZoneSameInstant(ZoneId.of("America/La_Paz")).format(fechaFmt);
                String hora = agendaEvento.getInicio().atZoneSameInstant(ZoneId.of("America/La_Paz")).format(horaFmt);

                List<String> nombresServicios = agendaEventoDetalleRepository.findByAgendaEventoId(agendaEvento.getId())
                        .stream()
                        .map(d -> d.getServicio() != null ? d.getServicio().getNombre() : (d.getComboServicio() != null ? d.getComboServicio().getNombre() : "Servicio"))
                        .toList();

                String empleadoNombre = agendaEventoEmpleadoRepository.findByAgendaEventoId(agendaEvento.getId())
                        .stream().findFirst()
                        .map(e -> e.getEmpleado().getNombre())
                        .orElse("Tu estilista");

                emailService.sendCitaConfirmacion(
                        cliente.getEmail(),
                        cliente.getNombre(),
                        fecha,
                        hora,
                        nombresServicios,
                        empleadoNombre,
                        sucursal.getNombre()
                );
            } catch (Exception ex) {
                logService.error("No se pudo enviar email de confirmación de cita: " + ex.getMessage());
            }
        }

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

            if (tieneServicio == tieneCombo) {
                log.error("Error en detalle AgendaEvento. Debe tener servicio_id o combo_servicio_id, pero no ambos");
                logService.error("Error en detalle AgendaEvento. Debe tener servicio_id o combo_servicio_id, pero no ambos");
                throw new OperationException("Cada detalle debe tener servicio_id o combo_servicio_id, pero no ambos");
            }
            if (detalle.getDuracionEstimadaMinutos() == null || detalle.getDuracionEstimadaMinutos() <= 0) {
                log.error("Error en detalle AgendaEvento. duracion_estimada_minutos debe ser mayor que cero");
                logService.error("Error en detalle AgendaEvento. duracion_estimada_minutos debe ser mayor que cero");
                throw new OperationException("duracion_estimada_minutos debe ser mayor que cero");
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
        return agendaEventoRepository.findAll().stream()
                .map(ae -> {
                    AgendaEventoResponseDto dto = new AgendaEventoResponseDto(ae);
                    List<AgendaEventoResponseDto.DetalleDto> detalles = agendaEventoDetalleRepository.findByAgendaEventoId(ae.getId()).stream()
                            .map(d -> new AgendaEventoResponseDto.DetalleDto(
                                    d.getServicio() != null ? d.getServicio().getId() : null,
                                    d.getServicio() != null ? d.getServicio().getNombre() : "Combo",
                                    d.getPrecioAcordado() != null ? d.getPrecioAcordado().doubleValue() : 0.0,
                                    d.getDuracionEstimadaMinutos()
                            ))
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
                            .map(d -> new AgendaEventoResponseDto.DetalleDto(
                                    d.getServicio() != null ? d.getServicio().getId() : null,
                                    d.getServicio() != null ? d.getServicio().getNombre() : "Combo",
                                    d.getPrecioAcordado() != null ? d.getPrecioAcordado().doubleValue() : 0.0,
                                    d.getDuracionEstimadaMinutos()
                            ))
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
                detalle.setDuracionEstimadaMinutos(detalleDto.getDuracionEstimadaMinutos());
                detalle.setPrecioAcordado(detalleDto.getPrecioAcordado());
                detalle.setNotas(detalleDto.getNotas());

                if (detalleDto.getServicioId() != null && !detalleDto.getServicioId().isBlank()) {
                    Servicio servicio = servicioRepository.findById(detalleDto.getServicioId())
                            .orElseThrow(() -> new OperationException("Servicio no encontrado con ID: " + detalleDto.getServicioId()));
                    detalle.setServicio(servicio);
                } else {
                    ComboServicio combo = comboServicioRepository.findById(detalleDto.getComboServicioId())
                            .orElseThrow(() -> new OperationException("Combo no encontrado con ID: " + detalleDto.getComboServicioId()));
                    detalle.setComboServicio(combo);
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
        agendaEvento.setEstado(nuevoEstado);
        agendaEvento = agendaEventoRepository.save(agendaEvento);
        logService.info("Estado de AgendaEvento actualizado a " + nuevoEstado + " para id: " + id);
        return new AgendaEventoResponseDto(agendaEvento);
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

        // Agregar detalles de los productos vendidos (si los hay)
        if (request.productoIds() != null) {
            for (String productoId : request.productoIds()) {
                if (productoId == null || productoId.isBlank()) continue;
                Producto producto = productoRepository.findById(productoId)
                        .orElseThrow(() -> new OperationException("Producto no encontrado con ID: " + productoId));

                VentaDetalleRequestDto detDto = new VentaDetalleRequestDto();
                detDto.setTipoItem(TipoItemVenta.PRODUCTO);
                detDto.setProductoId(producto.getId());
                detDto.setEmpleadoId(empleado.getId());
                detDto.setCantidad(1);
                detDto.setPrecioUnitario(producto.getPrecioVenta() != null ? producto.getPrecioVenta() : BigDecimal.ZERO);
                detDto.setDescuento(BigDecimal.ZERO);
                detDto.setNotas("Producto vendido en Walk-in");
                detallesVenta.add(detDto);
                totalVenta = totalVenta.add(detDto.getPrecioUnitario());
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

        // Crear venta (esto gestiona internamente la reducción de stock)
        ventaService.crear(ventaRequest);

        logService.info("Walk-in registrado con éxito. Cita ID: " + agendaEvento.getId());
    }

}
