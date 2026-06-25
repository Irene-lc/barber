package edu.upb.barber.service;

import edu.upb.barber.repository.EmpleadoRepository;
import edu.upb.barber.repository.HorarioEmpleadoFechaRepository;
import edu.upb.barber.repository.SucursalRepository;
import edu.upb.barber.repository.dto.request.HorarioEmpleadoFechaRequestDto;
import edu.upb.barber.repository.dto.response.HorarioEmpleadoFechaResponseDto;
import edu.upb.barber.repository.entity.Empleado;
import edu.upb.barber.repository.entity.HorarioEmpleadoFecha;
import edu.upb.barber.repository.entity.Sucursal;
import edu.upb.barber.service.exception.OperationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class HorarioEmpleadoFechaService {

    private final HorarioEmpleadoFechaRepository horarioEmpleadoFechaRepository;
    private final EmpleadoRepository empleadoRepository;
    private final SucursalRepository sucursalRepository;
    private final LogService logService;

    @Transactional(readOnly = true)
    public List<HorarioEmpleadoFechaResponseDto> listar(LocalDate desde, LocalDate hasta) {
        List<HorarioEmpleadoFecha> horarios = (desde != null && hasta != null)
                ? horarioEmpleadoFechaRepository.findByFechaBetween(desde, hasta)
                : horarioEmpleadoFechaRepository.findAll();
        return horarios.stream().map(HorarioEmpleadoFechaResponseDto::new).toList();
    }

    @Transactional(readOnly = true)
    public Optional<HorarioEmpleadoFechaResponseDto> findById(String id) {
        return horarioEmpleadoFechaRepository.findById(id).map(HorarioEmpleadoFechaResponseDto::new);
    }

    @Transactional
    public HorarioEmpleadoFechaResponseDto save(HorarioEmpleadoFechaRequestDto dto) throws Exception {
        validarDto(dto);

        Empleado empleado = empleadoRepository.findById(dto.getEmpleadoId())
                .orElseThrow(() -> new OperationException("Empleado no encontrado con id: " + dto.getEmpleadoId()));
        Sucursal sucursal = sucursalRepository.findById(dto.getSucursalId())
                .orElseThrow(() -> new OperationException("Sucursal no encontrada con id: " + dto.getSucursalId()));

        HorarioEmpleadoFecha horario = horarioEmpleadoFechaRepository
                .findByEmpleadoIdAndSucursalIdAndFecha(dto.getEmpleadoId(), dto.getSucursalId(), dto.getFecha())
                .stream()
                .filter(HorarioEmpleadoFecha::isActivo)
                .findFirst()
                .orElseGet(HorarioEmpleadoFecha::new);

        horario.setEmpleado(empleado);
        horario.setSucursal(sucursal);
        horario.setFecha(dto.getFecha());
        horario.setHoraInicio(dto.getHoraInicio());
        horario.setHoraFin(dto.getHoraFin());
        if (dto.getActivo() != null) {
            horario.setActivo(dto.getActivo());
        }

        logService.info("HorarioEmpleadoFecha guardado para empleado: " + dto.getEmpleadoId());
        return new HorarioEmpleadoFechaResponseDto(horarioEmpleadoFechaRepository.save(horario));
    }

    @Transactional
    public HorarioEmpleadoFechaResponseDto update(String id, HorarioEmpleadoFechaRequestDto dto) throws Exception {
        HorarioEmpleadoFecha horario = horarioEmpleadoFechaRepository.findById(id)
                .orElseThrow(() -> new OperationException("HorarioEmpleadoFecha no encontrado con id: " + id));
        validarDto(dto);
        validarUnicoHorarioDia(dto, id);

        horario.setFecha(dto.getFecha());
        horario.setHoraInicio(dto.getHoraInicio());
        horario.setHoraFin(dto.getHoraFin());
        if (dto.getActivo() != null) {
            horario.setActivo(dto.getActivo());
        }
        if (dto.getEmpleadoId() != null && !dto.getEmpleadoId().isBlank()) {
            Empleado empleado = empleadoRepository.findById(dto.getEmpleadoId())
                    .orElseThrow(() -> new OperationException("Empleado no encontrado con id: " + dto.getEmpleadoId()));
            horario.setEmpleado(empleado);
        }
        if (dto.getSucursalId() != null && !dto.getSucursalId().isBlank()) {
            Sucursal sucursal = sucursalRepository.findById(dto.getSucursalId())
                    .orElseThrow(() -> new OperationException("Sucursal no encontrada con id: " + dto.getSucursalId()));
            horario.setSucursal(sucursal);
        }

        return new HorarioEmpleadoFechaResponseDto(horarioEmpleadoFechaRepository.save(horario));
    }

    @Transactional
    public void delete(String id) throws Exception {
        if (!horarioEmpleadoFechaRepository.existsById(id)) {
            throw new OperationException("HorarioEmpleadoFecha no encontrado con id: " + id);
        }
        horarioEmpleadoFechaRepository.deleteById(id);
    }

    private void validarDto(HorarioEmpleadoFechaRequestDto dto) throws Exception {
        if (dto.getEmpleadoId() == null || dto.getEmpleadoId().isBlank()) {
            throw new OperationException("El campo empleado_id es requerido");
        }
        if (dto.getSucursalId() == null || dto.getSucursalId().isBlank()) {
            throw new OperationException("El campo sucursal_id es requerido");
        }
        if (dto.getFecha() == null) {
            throw new OperationException("El campo fecha es requerido");
        }
        if (dto.getHoraInicio() == null || dto.getHoraFin() == null) {
            throw new OperationException("Los campos hora_inicio y hora_fin son requeridos");
        }
        if (!dto.getHoraInicio().isBefore(dto.getHoraFin())) {
            throw new OperationException("hora_inicio debe ser anterior a hora_fin");
        }
    }

    private void validarUnicoHorarioDia(HorarioEmpleadoFechaRequestDto dto, String excludeId) throws OperationException {
        boolean existe = horarioEmpleadoFechaRepository
                .findByEmpleadoIdAndSucursalIdAndFecha(dto.getEmpleadoId(), dto.getSucursalId(), dto.getFecha())
                .stream()
                .anyMatch(h -> h.isActivo() && (excludeId == null || !h.getId().equals(excludeId)));
        if (existe) {
            throw new OperationException("El empleado ya tiene un horario asignado para esa fecha en esta sucursal");
        }
    }
}
