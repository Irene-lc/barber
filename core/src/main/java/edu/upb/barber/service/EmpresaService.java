package edu.upb.barber.service;

import edu.upb.barber.repository.dto.request.EmpresaRequest;
import edu.upb.barber.repository.dto.response.EmpresaResponse;
import edu.upb.barber.repository.EmpresaRepository;
import edu.upb.barber.repository.entity.Empresa;
import edu.upb.barber.service.exception.OperationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class EmpresaService {
    private final EmpresaRepository repository;

    @Transactional(readOnly = true)
    public List<EmpresaResponse> listar() {
//        return this.repository.listarEmpresas().stream().map(EmpresaResponse::fromEntity).toList();
        return this.repository.listarEmpresas();
    }

    @Transactional(readOnly = true)
    public EmpresaResponse buscarPorId(String id) throws Exception {
        return repository.findById(id)
                .map(EmpresaResponse::fromEntity)
                .orElseThrow(() -> new Exception("Empresa no encontrada con id: " + id));
    }

    @Transactional
    public void guardar(EmpresaRequest empresa) {
        if (empresa.getNombre() == null || empresa.getNombre().isBlank()) {
            log.error("Error al guardar empresa, el campo nombre es nulo");
            throw new OperationException("El campo nombre es null");
        }
        if (empresa.getEmail() == null || empresa.getEmail().isBlank()) {
            log.error("Error al guardar empresa, el campo email es nulo");
            throw new OperationException("El campo email es null");
        }
        Empresa entity = new Empresa();
        mapearDesdeDto(empresa, entity);
        this.repository.save(entity);
    }

    @Transactional
    public void actualizar(String id, EmpresaRequest dto) throws Exception {
        Empresa entity = repository.findById(id)
                .orElseThrow(() -> new Exception("Empresa no encontrada con id: " + id));
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void eliminar(String id) throws Exception {
        Empresa entity = repository.findById(id)
                .orElseThrow(() -> new Exception("Empresa no encontrada con id: " + id));
        entity.setActiva(false);
        repository.save(entity);
    }

    private void mapearDesdeDto(EmpresaRequest dto, Empresa entity) {
        entity.setNombre(dto.getNombre());
        entity.setRazonSocial(dto.getRazonSocial());
        entity.setNit(dto.getNit());
        entity.setTelefono(dto.getTelefono());
        entity.setEmail(dto.getEmail());
        entity.setActiva(dto.isActiva());
    }
}
