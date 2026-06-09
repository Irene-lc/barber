package edu.upb.barber.service;

import edu.upb.barber.repository.*;
import edu.upb.barber.repository.dto.request.AgendaEventoCreateRequestDto;
import edu.upb.barber.repository.dto.request.AgendaEventoDetalleCreateDto;
import edu.upb.barber.repository.dto.request.AgendaEventoEmpleadoCreateDto;
import edu.upb.barber.repository.dto.response.AgendaEventoCreateResponseDto;
import edu.upb.barber.repository.dto.response.AgendaEventoDetalleResponseDto;
import edu.upb.barber.repository.dto.response.AgendaEventoEmpleadoResponseDto;
import edu.upb.barber.repository.dto.response.AgendaEventoResponseDto;
import edu.upb.barber.repository.entity.*;
import edu.upb.barber.repository.entity.enums.EstadoEvento;
import edu.upb.barber.repository.entity.enums.RolEmpleadoEvento;
import edu.upb.barber.repository.entity.enums.TipoEvento;
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

    @Transactional
    public AgendaEventoCreateResponseDto crear(AgendaEventoCreateRequestDto request) throws Exception {

        validarRequestBase(request);

        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new Exception("Sucursal no encontrada con ID: " + request.getSucursalId()));

        Cliente cliente = null;
        if (request.getClienteId() != null && !request.getClienteId().isBlank()) {
            cliente = clienteRepository.findById(request.getClienteId())
                    .orElseThrow(() -> new Exception("Cliente no encontrado con ID: " + request.getClienteId()));
        }

        Mascota mascota = null;
        if (request.getMascotaId() != null && !request.getMascotaId().isBlank()) {
            mascota = mascotaRepository.findById(request.getMascotaId())
                    .orElseThrow(() -> new Exception("Mascota no encontrada con ID: " + request.getMascotaId()));
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
                            .orElseThrow(() -> new Exception("Servicio no encontrado con ID: " + detalleDto.getServicioId()));
                    detalle.setServicio(servicio);
                } else {
                    ComboServicio combo = comboServicioRepository.findById(detalleDto.getComboServicioId())
                            .orElseThrow(() -> new Exception("Combo no encontrado con ID: " + detalleDto.getComboServicioId()));
                    detalle.setComboServicio(combo);
                }
                agendaEventoDetalleRepository.save(detalle);
            }
        }

        if (request.getEmpleados() != null) {
            for (AgendaEventoEmpleadoCreateDto empleadoDto : request.getEmpleados()) {
                Empleado empleado = empleadoRepository.findById(empleadoDto.getEmpleadoId())
                        .orElseThrow(() -> new Exception("Empleado no encontrado con ID: " + empleadoDto.getEmpleadoId()));

                AgendaEventoEmpleado agendaEventoEmpleado = new AgendaEventoEmpleado();
                agendaEventoEmpleado.setAgendaEvento(agendaEvento);
                agendaEventoEmpleado.setEmpleado(empleado);
                agendaEventoEmpleado.setRolEnEvento(
                        empleadoDto.getRolEnEvento() == null ? RolEmpleadoEvento.RESPONSABLE : empleadoDto.getRolEnEvento()
                );
                agendaEventoEmpleadoRepository.save(agendaEventoEmpleado);
            }
        }

        AgendaEventoCreateResponseDto response = new AgendaEventoCreateResponseDto();
        response.setAgendaEventoId(agendaEvento.getId());
        response.setEstado(agendaEvento.getEstado());
        return response;
    }

    private void validarRequestBase(AgendaEventoCreateRequestDto request) throws Exception {
        if (request == null) {
            throw new Exception("Request no puede ser null");
        }
        if (request.getSucursalId() == null || request.getSucursalId().isBlank()) {
            throw new Exception("sucursal_id es requerido");
        }
        if (request.getTipoEvento() == null) {
            throw new Exception("tipo_evento es requerido");
        }
        if (request.getInicio() == null || request.getFin() == null) {
            throw new Exception("inicio y fin son requeridos");
        }
        if (!request.getInicio().isBefore(request.getFin())) {
            throw new Exception("inicio debe ser menor que fin");
        }
    }

    private void validarReglasPorTipo(AgendaEventoCreateRequestDto request) throws Exception {
        if (request.getTipoEvento() == TipoEvento.CITA) {
            if (request.getEmpleados() == null || request.getEmpleados().isEmpty()) {
                throw new Exception("Para tipo CITA, empleados es requerido");
            }
            if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
                throw new Exception("Para tipo CITA, detalles es requerido");
            }
        } else if (request.getDetalles() != null && !request.getDetalles().isEmpty()) {
            throw new Exception("detalles solo aplica para tipo CITA");
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
                throw new Exception("Cada detalle debe tener servicio_id o combo_servicio_id, pero no ambos");
            }
            if (detalle.getDuracionEstimadaMinutos() == null || detalle.getDuracionEstimadaMinutos() <= 0) {
                throw new Exception("duracion_estimada_minutos debe ser mayor que cero");
            }
            if (detalle.getPrecioAcordado() == null || detalle.getPrecioAcordado().compareTo(BigDecimal.ZERO) < 0) {
                throw new Exception("precio_acordado debe ser mayor o igual a cero");
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
                throw new Exception("empleado_id es requerido en cada asignacion");
            }
            if (!ids.add(asignacion.getEmpleadoId())) {
                throw new Exception("Empleado duplicado en asignaciones: " + asignacion.getEmpleadoId());
            }

            Empleado empleado = empleadoRepository.findById(asignacion.getEmpleadoId())
                    .orElseThrow(() -> new Exception("Empleado no encontrado con ID: " + asignacion.getEmpleadoId()));
            if (!empleado.isActivo()) {
                throw new Exception("Empleado inactivo: " + empleado.getId());
            }

            boolean pertenece = empleadoSucursalRepository
                    .existsByEmpleadoIdAndSucursalIdAndActivoTrue(asignacion.getEmpleadoId(), sucursalId);
            if (!pertenece) {
                throw new Exception("Empleado " + asignacion.getEmpleadoId() + " no pertenece a la sucursal " + sucursalId);
            }

            boolean enConflicto = agendaEventoEmpleadoRepository.existsConflictoHorarioEmpleado(
                    asignacion.getEmpleadoId(),
                    request.getInicio(),
                    request.getFin(),
                    Arrays.asList(EstadoEvento.CANCELADO, EstadoEvento.NO_SHOW)
            );
            if (enConflicto) {
                throw new Exception("Conflicto de horario para empleado: " + asignacion.getEmpleadoId());
            }
        }
    }
    @Transactional(readOnly = true)
    public List<AgendaEventoResponseDto> listar() {
        return agendaEventoRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<AgendaEventoResponseDto> findById(String id) {
        return agendaEventoRepository.findById(id)
                .map(this::toResponse);
    }

    private AgendaEventoResponseDto toResponse(AgendaEvento agendaEvento) {
        AgendaEventoResponseDto response = new AgendaEventoResponseDto(agendaEvento);
        response.setDetalles(agendaEventoDetalleRepository.findByAgendaEventoId(agendaEvento.getId()).stream()
                .map(AgendaEventoDetalleResponseDto::new)
                .collect(Collectors.toList()));
        response.setEmpleados(agendaEventoEmpleadoRepository.findByAgendaEventoId(agendaEvento.getId()).stream()
                .map(AgendaEventoEmpleadoResponseDto::new)
                .collect(Collectors.toList()));
        return response;
    }

    @Transactional
    public AgendaEventoCreateResponseDto update(String id, AgendaEventoCreateRequestDto request) throws Exception {
        AgendaEvento agendaEvento = agendaEventoRepository.findById(id)
                .orElseThrow(() -> new Exception("AgendaEvento no encontrado con ID: " + id));

        validarRequestBase(request);

        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new Exception("Sucursal no encontrada con ID: " + request.getSucursalId()));

        Cliente cliente = null;
        if (request.getClienteId() != null && !request.getClienteId().isBlank()) {
            cliente = clienteRepository.findById(request.getClienteId())
                    .orElseThrow(() -> new Exception("Cliente no encontrado con ID: " + request.getClienteId()));
        }

        Mascota mascota = null;
        if (request.getMascotaId() != null && !request.getMascotaId().isBlank()) {
            mascota = mascotaRepository.findById(request.getMascotaId())
                    .orElseThrow(() -> new Exception("Mascota no encontrada con ID: " + request.getMascotaId()));
        }

        validarReglasPorTipo(request);
        validarAsignacionesParaUpdate(request.getEmpleados(), request.getSucursalId(), request, id);
        validarDetalles(request.getDetalles(), request.getTipoEvento());

        // Limpiar detalles y asignaciones anteriores
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
                            .orElseThrow(() -> new Exception("Servicio no encontrado con ID: " + detalleDto.getServicioId()));
                    detalle.setServicio(servicio);
                } else {
                    ComboServicio combo = comboServicioRepository.findById(detalleDto.getComboServicioId())
                            .orElseThrow(() -> new Exception("Combo no encontrado con ID: " + detalleDto.getComboServicioId()));
                    detalle.setComboServicio(combo);
                }
                agendaEventoDetalleRepository.save(detalle);
            }
        }

        if (request.getEmpleados() != null) {
            for (AgendaEventoEmpleadoCreateDto empleadoDto : request.getEmpleados()) {
                Empleado empleado = empleadoRepository.findById(empleadoDto.getEmpleadoId())
                        .orElseThrow(() -> new Exception("Empleado no encontrado con ID: " + empleadoDto.getEmpleadoId()));

                AgendaEventoEmpleado agendaEventoEmpleado = new AgendaEventoEmpleado();
                agendaEventoEmpleado.setAgendaEvento(agendaEvento);
                agendaEventoEmpleado.setEmpleado(empleado);
                agendaEventoEmpleado.setRolEnEvento(
                        empleadoDto.getRolEnEvento() == null ? RolEmpleadoEvento.RESPONSABLE : empleadoDto.getRolEnEvento()
                );
                agendaEventoEmpleadoRepository.save(agendaEventoEmpleado);
            }
        }

        AgendaEventoCreateResponseDto response = new AgendaEventoCreateResponseDto();
        response.setAgendaEventoId(agendaEvento.getId());
        response.setEstado(agendaEvento.getEstado());
        return response;
    }

    @Transactional
    public void delete(String id) throws Exception {
        AgendaEvento agendaEvento = agendaEventoRepository.findById(id)
                .orElseThrow(() -> new Exception("AgendaEvento no encontrado con ID: " + id));

        agendaEventoDetalleRepository.deleteByAgendaEventoId(id);
        agendaEventoEmpleadoRepository.deleteByAgendaEventoId(id);
        agendaEventoRepository.delete(agendaEvento);
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
                throw new Exception("empleado_id es requerido en cada asignacion");
            }
            if (!ids.add(asignacion.getEmpleadoId())) {
                throw new Exception("Empleado duplicado en asignaciones: " + asignacion.getEmpleadoId());
            }

            Empleado empleado = empleadoRepository.findById(asignacion.getEmpleadoId())
                    .orElseThrow(() -> new Exception("Empleado no encontrado con ID: " + asignacion.getEmpleadoId()));
            if (!empleado.isActivo()) {
                throw new Exception("Empleado inactivo: " + empleado.getId());
            }

            boolean pertenece = empleadoSucursalRepository
                    .existsByEmpleadoIdAndSucursalIdAndActivoTrue(asignacion.getEmpleadoId(), sucursalId);
            if (!pertenece) {
                throw new Exception("Empleado " + asignacion.getEmpleadoId() + " no pertenece a la sucursal " + sucursalId);
            }

            boolean enConflicto = agendaEventoEmpleadoRepository.existsConflictoHorarioEmpleadoExcludingEvent(
                    asignacion.getEmpleadoId(),
                    excludeEventoId,
                    request.getInicio(),
                    request.getFin(),
                    Arrays.asList(EstadoEvento.CANCELADO, EstadoEvento.NO_SHOW)
            );
            if (enConflicto) {
                throw new Exception("Conflicto de horario para empleado: " + asignacion.getEmpleadoId());
            }
        }
    }

}
