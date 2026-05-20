package edu.upb.barber.service;

import edu.upb.barber.repository.dto.request.SucursalRequest;
import edu.upb.barber.repository.dto.response.SucursalResponse;
import edu.upb.barber.repository.EmpresaRepository;
import edu.upb.barber.repository.SucursalRepository;
import edu.upb.barber.repository.entity.Sucursal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class SucursalService {
    private final SucursalRepository repository;
    private final EmpresaRepository empresaRepository;

    @Transactional(readOnly = true)
    public List<SucursalResponse> listar() {
        return repository.findAll().stream().map(SucursalResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public SucursalResponse buscarPorId(String id) throws Exception {
        return repository.findById(id)
                .map(SucursalResponse::fromEntity)
                .orElseThrow(() -> new Exception("Sucursal no encontrada con id: " + id));
    }

    @Transactional
    public void guardar(SucursalRequest dto) throws Exception {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new Exception("El campo nombre es requerido");
        }
        Sucursal entity = new Sucursal();
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void actualizar(String id, SucursalRequest dto) throws Exception {
        Sucursal entity = repository.findById(id)
                .orElseThrow(() -> new Exception("Sucursal no encontrada con id: " + id));
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void eliminar(String id) throws Exception {
        Sucursal entity = repository.findById(id)
                .orElseThrow(() -> new Exception("Sucursal no encontrada con id: " + id));
        entity.setActiva(false);
        repository.save(entity);
    }

    private void mapearDesdeDto(SucursalRequest dto, Sucursal entity) throws Exception {
        entity.setEmpresa(empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new Exception("Empresa no encontrada con id: " + dto.getEmpresaId())));
        entity.setNombre(dto.getNombre());
        entity.setDireccion(dto.getDireccion());
        entity.setTelefono(dto.getTelefono());
        entity.setZonaHoraria(dto.getZonaHoraria() != null ? dto.getZonaHoraria() : "America/La_Paz");
        entity.setActiva(dto.isActiva());
    }
}
