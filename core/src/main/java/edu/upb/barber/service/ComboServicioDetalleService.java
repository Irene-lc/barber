package edu.upb.barber.service;

import edu.upb.barber.dto.request.ComboServicioDetalleRequest;
import edu.upb.barber.dto.response.ComboServicioDetalleResponse;
import edu.upb.barber.repository.ComboServicioDetalleRepository;
import edu.upb.barber.repository.ComboServicioRepository;
import edu.upb.barber.repository.ServicioRepository;
import edu.upb.barber.repository.entity.ComboServicioDetalle;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ComboServicioDetalleService {
    private final ComboServicioDetalleRepository repository;
    private final ComboServicioRepository comboServicioRepository;
    private final ServicioRepository servicioRepository;

    @Transactional(readOnly = true)
    public List<ComboServicioDetalleResponse> listar() {
        return repository.findAll().stream().map(ComboServicioDetalleResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public ComboServicioDetalleResponse buscarPorId(String id) throws Exception {
        return repository.findById(id)
                .map(ComboServicioDetalleResponse::fromEntity)
                .orElseThrow(() -> new Exception("ComboServicioDetalle no encontrado con id: " + id));
    }

    @Transactional
    public void guardar(ComboServicioDetalleRequest dto) throws Exception {
        ComboServicioDetalle entity = new ComboServicioDetalle();
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void actualizar(String id, ComboServicioDetalleRequest dto) throws Exception {
        ComboServicioDetalle entity = repository.findById(id)
                .orElseThrow(() -> new Exception("ComboServicioDetalle no encontrado con id: " + id));
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void eliminar(String id) throws Exception {
        if (!repository.existsById(id)) {
            throw new Exception("ComboServicioDetalle no encontrado con id: " + id);
        }
        repository.deleteById(id);
    }

    private void mapearDesdeDto(ComboServicioDetalleRequest dto, ComboServicioDetalle entity) throws Exception {
        entity.setComboServicio(comboServicioRepository.findById(dto.getComboServicioId())
                .orElseThrow(() -> new Exception("ComboServicio no encontrado con id: " + dto.getComboServicioId())));
        entity.setServicio(servicioRepository.findById(dto.getServicioId())
                .orElseThrow(() -> new Exception("Servicio no encontrado con id: " + dto.getServicioId())));
        entity.setOrdenEjecucion(dto.getOrdenEjecucion());
    }
}
