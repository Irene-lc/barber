package edu.upb.barber.service;

import edu.upb.barber.repository.EmpleadoRepository;
import edu.upb.barber.repository.HorarioEmpleadoRepository;
import edu.upb.barber.repository.SucursalRepository;
import edu.upb.barber.repository.dto.request.HorarioEmpleadoRequestDto;
import edu.upb.barber.repository.dto.response.HorarioEmpleadoResponseDto;
import edu.upb.barber.repository.entity.Empleado;
import edu.upb.barber.repository.entity.HorarioEmpleado;
import edu.upb.barber.repository.entity.Sucursal;
import edu.upb.barber.service.exception.OperationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class HorarioEmpleadoService {

    private final HorarioEmpleadoRepository horarioEmpleadoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final SucursalRepository sucursalRepository;
    private final LogService logService;

    @Transactional(readOnly = true)
    public List<HorarioEmpleadoResponseDto> listar() {
        return horarioEmpleadoRepository.findAll().stream()
                .map(HorarioEmpleadoResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<HorarioEmpleadoResponseDto> findById(String id) {
        return horarioEmpleadoRepository.findById(id)
                .map(HorarioEmpleadoResponseDto::new);
    }

    @Transactional
    public HorarioEmpleadoResponseDto save(HorarioEmpleadoRequestDto dto) throws Exception {
        validarDto(dto);

        Empleado empleado = empleadoRepository.findById(dto.getEmpleadoId())
                .orElseThrow(() -> new OperationException("Empleado no encontrado con id: " + dto.getEmpleadoId()));

        Sucursal sucursal = sucursalRepository.findById(dto.getSucursalId())
                .orElseThrow(() -> new OperationException("Sucursal no encontrada con id: " + dto.getSucursalId()));

        HorarioEmpleado horario = new HorarioEmpleado();
        horario.setEmpleado(empleado);
        horario.setSucursal(sucursal);
        horario.setDiaSemana(dto.getDiaSemana());
        horario.setHoraInicio(dto.getHoraInicio());
        horario.setHoraFin(dto.getHoraFin());

        if (dto.getActivo() != null) {
            horario.setActivo(dto.getActivo());
        }

        logService.info("HorarioEmpleado guardado exitosamente para empleado: " + dto.getEmpleadoId());
        return new HorarioEmpleadoResponseDto(horarioEmpleadoRepository.save(horario));
    }

    @Transactional
    public HorarioEmpleadoResponseDto update(String horarioId, HorarioEmpleadoRequestDto dto) throws Exception {
        HorarioEmpleado horario = horarioEmpleadoRepository.findById(horarioId)
                .orElseThrow(() -> new OperationException("HorarioEmpleado no encontrado con id: " + horarioId));

        validarDto(dto);

        horario.setDiaSemana(dto.getDiaSemana());
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

        logService.info("HorarioEmpleado actualizado exitosamente: " + horarioId);
        return new HorarioEmpleadoResponseDto(horarioEmpleadoRepository.save(horario));
    }

    @Transactional
    public void delete(String id) throws Exception {
        if (!horarioEmpleadoRepository.existsById(id)) {
            log.error("Error al eliminar HorarioEmpleado. No encontrado con id: {}", id);
            logService.error("Error al eliminar HorarioEmpleado. No encontrado con id: " + id);
            throw new OperationException("HorarioEmpleado no encontrado con id: " + id);
        }
        horarioEmpleadoRepository.deleteById(id);
        logService.info("HorarioEmpleado eliminado exitosamente: " + id);
    }

    private void validarDto(HorarioEmpleadoRequestDto dto) throws Exception {
        if (dto.getDiaSemana() == null) {
            log.error("Error en HorarioEmpleado. El campo dia_semana es requerido");
            logService.error("Error en HorarioEmpleado. El campo dia_semana es requerido");
            throw new OperationException("El campo dia_semana es requerido");
        }
        if (dto.getHoraInicio() == null || dto.getHoraFin() == null) {
            log.error("Error en HorarioEmpleado. Los campos hora_inicio y hora_fin son requeridos");
            logService.error("Error en HorarioEmpleado. Los campos hora_inicio y hora_fin son requeridos");
            throw new OperationException("Los campos hora_inicio y hora_fin son requeridos");
        }
        if (!dto.getHoraInicio().isBefore(dto.getHoraFin())) {
            log.error("Error en HorarioEmpleado. hora_inicio debe ser anterior a hora_fin");
            logService.error("Error en HorarioEmpleado. hora_inicio debe ser anterior a hora_fin");
            throw new OperationException("hora_inicio debe ser anterior a hora_fin");
        }
    }
}
