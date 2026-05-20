package edu.upb.barber.service;

import edu.upb.barber.repository.dto.request.EmpleadoSucursalRequest;
import edu.upb.barber.repository.dto.response.EmpleadoSucursalResponse;
import edu.upb.barber.repository.EmpleadoRepository;
import edu.upb.barber.repository.EmpleadoSucursalRepository;
import edu.upb.barber.repository.SucursalRepository;
import edu.upb.barber.repository.entity.EmpleadoSucursal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class EmpleadoSucursalService {
    private final EmpleadoSucursalRepository repository;
    private final EmpleadoRepository empleadoRepository;
    private final SucursalRepository sucursalRepository;

    @Transactional(readOnly = true)
    public List<EmpleadoSucursalResponse> listar() {
        return repository.findAll().stream().map(EmpleadoSucursalResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public EmpleadoSucursalResponse buscarPorId(String id) throws Exception {
        return repository.findById(id)
                .map(EmpleadoSucursalResponse::fromEntity)
                .orElseThrow(() -> new Exception("EmpleadoSucursal no encontrado con id: " + id));
    }

    @Transactional
    public void guardar(EmpleadoSucursalRequest dto) throws Exception {
        EmpleadoSucursal entity = new EmpleadoSucursal();
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void actualizar(String id, EmpleadoSucursalRequest dto) throws Exception {
        EmpleadoSucursal entity = repository.findById(id)
                .orElseThrow(() -> new Exception("EmpleadoSucursal no encontrado con id: " + id));
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void eliminar(String id) throws Exception {
        EmpleadoSucursal entity = repository.findById(id)
                .orElseThrow(() -> new Exception("EmpleadoSucursal no encontrado con id: " + id));
        entity.setActivo(false);
        repository.save(entity);
    }

    private void mapearDesdeDto(EmpleadoSucursalRequest dto, EmpleadoSucursal entity) throws Exception {
        entity.setEmpleado(empleadoRepository.findById(dto.getEmpleadoId())
                .orElseThrow(() -> new Exception("Empleado no encontrado con id: " + dto.getEmpleadoId())));
        entity.setSucursal(sucursalRepository.findById(dto.getSucursalId())
                .orElseThrow(() -> new Exception("Sucursal no encontrada con id: " + dto.getSucursalId())));
        entity.setActivo(dto.isActivo());
    }
}
