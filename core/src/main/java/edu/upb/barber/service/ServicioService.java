package edu.upb.barber.service;

import edu.upb.barber.dto.request.ServicioRequest;
import edu.upb.barber.dto.response.ServicioResponse;
import edu.upb.barber.repository.EmpresaRepository;
import edu.upb.barber.repository.ServicioRepository;
import edu.upb.barber.repository.entity.Servicio;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ServicioService {
    private final ServicioRepository repository;
    private final EmpresaRepository empresaRepository;

    @Transactional(readOnly = true)
    public List<ServicioResponse> listar() {
        return repository.findAll().stream().map(ServicioResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public ServicioResponse buscarPorId(String id) throws Exception {
        return repository.findById(id)
                .map(ServicioResponse::fromEntity)
                .orElseThrow(() -> new Exception("Servicio no encontrado con id: " + id));
    }

    @Transactional
    public void guardar(ServicioRequest dto) throws Exception {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new Exception("El campo nombre es requerido");
        }
        Servicio entity = new Servicio();
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void actualizar(String id, ServicioRequest dto) throws Exception {
        Servicio entity = repository.findById(id)
                .orElseThrow(() -> new Exception("Servicio no encontrado con id: " + id));
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void eliminar(String id) throws Exception {
        Servicio entity = repository.findById(id)
                .orElseThrow(() -> new Exception("Servicio no encontrado con id: " + id));
        entity.setActivo(false);
        repository.save(entity);
    }

    private void mapearDesdeDto(ServicioRequest dto, Servicio entity) throws Exception {
        entity.setEmpresa(empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new Exception("Empresa no encontrada con id: " + dto.getEmpresaId())));
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setDestinatario(dto.getDestinatario());
        entity.setCategoria(dto.getCategoria());
        entity.setDuracionMinutos(dto.getDuracionMinutos());
        entity.setPrecioBase(dto.getPrecioBase());
        entity.setActivo(dto.isActivo());
    }
}
