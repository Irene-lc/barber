package edu.upb.barber.service;

import edu.upb.barber.repository.EmpleadoRepository;
import edu.upb.barber.repository.HorarioEmpleadoRepository;
import edu.upb.barber.repository.SucursalRepository;
import edu.upb.barber.repository.dto.request.HorarioEmpleadoRequestDto;
import edu.upb.barber.repository.entity.Empleado;
import edu.upb.barber.repository.entity.HorarioEmpleado;
import edu.upb.barber.repository.entity.Sucursal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class HorarioEmpleadoService {

    private final HorarioEmpleadoRepository horarioEmpleadoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final SucursalRepository sucursalRepository;

    @Transactional(readOnly = true)
    public List<HorarioEmpleado> listar() {
        return horarioEmpleadoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<HorarioEmpleado> findById(String id) {
        return horarioEmpleadoRepository.findById(id);
    }

    @Transactional
    public HorarioEmpleado save(HorarioEmpleadoRequestDto dto) throws Exception {

        validarDto(dto);

        Empleado empleado = empleadoRepository.findById(dto.getEmpleadoId())
                .orElseThrow(() -> new Exception("Empleado no encontrado con id: " + dto.getEmpleadoId()));

        Sucursal sucursal = sucursalRepository.findById(dto.getSucursalId())
                .orElseThrow(() -> new Exception("Sucursal no encontrada con id: " + dto.getSucursalId()));

        HorarioEmpleado horario = new HorarioEmpleado();
        horario.setEmpleado(empleado);
        horario.setSucursal(sucursal);
        horario.setDiaSemana(dto.getDiaSemana());
        horario.setHoraInicio(dto.getHoraInicio());
        horario.setHoraFin(dto.getHoraFin());

        if (dto.getActivo() != null) {
            horario.setActivo(dto.getActivo());
        }

        return horarioEmpleadoRepository.save(horario);
    }

    @Transactional
    public HorarioEmpleado update(String horarioId, HorarioEmpleadoRequestDto dto) throws Exception {

        HorarioEmpleado horario = horarioEmpleadoRepository.findById(horarioId)
                .orElseThrow(() -> new Exception("HorarioEmpleado no encontrado con id: " + horarioId));

        validarDto(dto);

        horario.setDiaSemana(dto.getDiaSemana());
        horario.setHoraInicio(dto.getHoraInicio());
        horario.setHoraFin(dto.getHoraFin());

        if (dto.getActivo() != null) {
            horario.setActivo(dto.getActivo());
        }

        return horarioEmpleadoRepository.save(horario);
    }

    @Transactional
    public void delete(String id) throws Exception {
        if (!horarioEmpleadoRepository.existsById(id)) {
            throw new Exception("HorarioEmpleado no encontrado con id: " + id);
        }
        horarioEmpleadoRepository.deleteById(id);
    }

    private void validarDto(HorarioEmpleadoRequestDto dto) throws Exception {
        if (dto.getDiaSemana() == null) {
            throw new Exception("El campo dia_semana es requerido");
        }
        if (dto.getHoraInicio() == null || dto.getHoraFin() == null) {
            throw new Exception("Los campos hora_inicio y hora_fin son requeridos");
        }
        if (!dto.getHoraInicio().isBefore(dto.getHoraFin())) {
            throw new Exception("hora_inicio debe ser anterior a hora_fin");
        }
    }
}
