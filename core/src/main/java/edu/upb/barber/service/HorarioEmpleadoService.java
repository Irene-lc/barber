package edu.upb.barber.service;

import edu.upb.barber.repository.dto.request.HorarioEmpleadoRequest;
import edu.upb.barber.repository.dto.response.HorarioEmpleadoResponse;
import edu.upb.barber.repository.EmpleadoRepository;
import edu.upb.barber.repository.HorarioEmpleadoRepository;
import edu.upb.barber.repository.SucursalRepository;
import edu.upb.barber.repository.entity.HorarioEmpleado;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class HorarioEmpleadoService {
    private final HorarioEmpleadoRepository repository;
    private final EmpleadoRepository empleadoRepository;
    private final SucursalRepository sucursalRepository;

    @Transactional(readOnly = true)
    public List<HorarioEmpleadoResponse> listar() {
        return repository.findAll().stream().map(HorarioEmpleadoResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public HorarioEmpleadoResponse buscarPorId(String id) throws Exception {
        return repository.findById(id)
                .map(HorarioEmpleadoResponse::fromEntity)
                .orElseThrow(() -> new Exception("HorarioEmpleado no encontrado con id: " + id));
    }

    @Transactional
    public void guardar(HorarioEmpleadoRequest dto) throws Exception {
        if (dto.getDiaSemana() == null) {
            throw new Exception("El campo diaSemana es requerido");
        }
        HorarioEmpleado entity = new HorarioEmpleado();
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void actualizar(String id, HorarioEmpleadoRequest dto) throws Exception {
        HorarioEmpleado entity = repository.findById(id)
                .orElseThrow(() -> new Exception("HorarioEmpleado no encontrado con id: " + id));
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void eliminar(String id) throws Exception {
        HorarioEmpleado entity = repository.findById(id)
                .orElseThrow(() -> new Exception("HorarioEmpleado no encontrado con id: " + id));
        entity.setActivo(false);
        repository.save(entity);
    }

    private void mapearDesdeDto(HorarioEmpleadoRequest dto, HorarioEmpleado entity) throws Exception {
        entity.setEmpleado(empleadoRepository.findById(dto.getEmpleadoId())
                .orElseThrow(() -> new Exception("Empleado no encontrado con id: " + dto.getEmpleadoId())));
        entity.setSucursal(sucursalRepository.findById(dto.getSucursalId())
                .orElseThrow(() -> new Exception("Sucursal no encontrada con id: " + dto.getSucursalId())));
        entity.setDiaSemana(dto.getDiaSemana());
        entity.setHoraInicio(dto.getHoraInicio());
        entity.setHoraFin(dto.getHoraFin());
        entity.setActivo(dto.isActivo());
    }
}
