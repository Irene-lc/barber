package edu.upb.barber.service;

import edu.upb.barber.repository.ServicioRepository;
import edu.upb.barber.repository.dto.request.ServicioRequestDto;
import edu.upb.barber.repository.entity.Servicio;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class ServicioService {

    private final ServicioRepository servicioRepository;

    @Transactional(readOnly = true)
    public List<Servicio> listar() {
        return servicioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Servicio> findById(String id) {
        return servicioRepository.findById(id);
    }

    @Transactional
    public void save(Servicio servicio) {
        servicioRepository.save(servicio);
    }

    @Transactional
    public void update(String servicioId, ServicioRequestDto dto) throws Exception {

        Servicio servicio = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new Exception("Servicio no encontrado con id: " + servicioId));

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new Exception("El campo nombre es requerido");
        }
        if (dto.getPrecio() == null || dto.getPrecio() < 0) {
            throw new Exception("El precio debe ser mayor o igual a cero");
        }
        if (dto.getDuracion() == null || dto.getDuracion() <= 0) {
            throw new Exception("La duracion debe ser mayor a cero");
        }

        servicio.setNombre(dto.getNombre());
        servicio.setDescripcion(dto.getDescripcion());
        servicio.setPrecioBase(BigDecimal.valueOf(dto.getPrecio()));
        servicio.setDuracionMinutos(dto.getDuracion());

        servicioRepository.save(servicio);
    }

    @Transactional
    public void delete(String id) {
        servicioRepository.deleteById(id);
    }
}
