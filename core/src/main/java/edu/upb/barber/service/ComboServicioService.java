package edu.upb.barber.service;

import edu.upb.barber.dto.request.ComboServicioRequest;
import edu.upb.barber.dto.response.ComboServicioResponse;
import edu.upb.barber.repository.ComboServicioRepository;
import edu.upb.barber.repository.EmpresaRepository;
import edu.upb.barber.repository.entity.ComboServicio;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ComboServicioService {
    private final ComboServicioRepository repository;
    private final EmpresaRepository empresaRepository;

    @Transactional(readOnly = true)
    public List<ComboServicioResponse> listar() {
        return repository.findAll().stream().map(ComboServicioResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public ComboServicioResponse buscarPorId(String id) throws Exception {
        return repository.findById(id)
                .map(ComboServicioResponse::fromEntity)
                .orElseThrow(() -> new Exception("ComboServicio no encontrado con id: " + id));
    }

    @Transactional
    public void guardar(ComboServicioRequest dto) throws Exception {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new Exception("El campo nombre es requerido");
        }
        ComboServicio entity = new ComboServicio();
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void actualizar(String id, ComboServicioRequest dto) throws Exception {
        ComboServicio entity = repository.findById(id)
                .orElseThrow(() -> new Exception("ComboServicio no encontrado con id: " + id));
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void eliminar(String id) throws Exception {
        ComboServicio entity = repository.findById(id)
                .orElseThrow(() -> new Exception("ComboServicio no encontrado con id: " + id));
        entity.setActivo(false);
        repository.save(entity);
    }

    private void mapearDesdeDto(ComboServicioRequest dto, ComboServicio entity) throws Exception {
        entity.setEmpresa(empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new Exception("Empresa no encontrada con id: " + dto.getEmpresaId())));
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setPrecio(dto.getPrecio());
        entity.setDuracionMinutos(dto.getDuracionMinutos());
        entity.setActivo(dto.isActivo());
    }
}
