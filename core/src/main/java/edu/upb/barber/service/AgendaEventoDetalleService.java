package edu.upb.barber.service;

import edu.upb.barber.dto.request.AgendaEventoDetalleRequest;
import edu.upb.barber.dto.response.AgendaEventoDetalleResponse;
import edu.upb.barber.repository.AgendaEventoDetalleRepository;
import edu.upb.barber.repository.AgendaEventoRepository;
import edu.upb.barber.repository.ComboServicioRepository;
import edu.upb.barber.repository.ServicioRepository;
import edu.upb.barber.repository.entity.AgendaEventoDetalle;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class AgendaEventoDetalleService {
    private final AgendaEventoDetalleRepository repository;
    private final AgendaEventoRepository agendaEventoRepository;
    private final ServicioRepository servicioRepository;
    private final ComboServicioRepository comboServicioRepository;

    @Transactional(readOnly = true)
    public List<AgendaEventoDetalleResponse> listar() {
        return repository.findAll().stream().map(AgendaEventoDetalleResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public AgendaEventoDetalleResponse buscarPorId(String id) throws Exception {
        return repository.findById(id)
                .map(AgendaEventoDetalleResponse::fromEntity)
                .orElseThrow(() -> new Exception("AgendaEventoDetalle no encontrado con id: " + id));
    }

    @Transactional
    public void guardar(AgendaEventoDetalleRequest dto) throws Exception {
        if (dto.getPrecioAcordado() == null) {
            throw new Exception("El campo precioAcordado es requerido");
        }
        AgendaEventoDetalle entity = new AgendaEventoDetalle();
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void actualizar(String id, AgendaEventoDetalleRequest dto) throws Exception {
        AgendaEventoDetalle entity = repository.findById(id)
                .orElseThrow(() -> new Exception("AgendaEventoDetalle no encontrado con id: " + id));
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void eliminar(String id) throws Exception {
        if (!repository.existsById(id)) {
            throw new Exception("AgendaEventoDetalle no encontrado con id: " + id);
        }
        repository.deleteById(id);
    }

    private void mapearDesdeDto(AgendaEventoDetalleRequest dto, AgendaEventoDetalle entity) throws Exception {
        entity.setAgendaEvento(agendaEventoRepository.findById(dto.getAgendaEventoId())
                .orElseThrow(() -> new Exception("AgendaEvento no encontrado con id: " + dto.getAgendaEventoId())));
        if (dto.getServicioId() != null) {
            entity.setServicio(servicioRepository.findById(dto.getServicioId())
                    .orElseThrow(() -> new Exception("Servicio no encontrado con id: " + dto.getServicioId())));
        }
        if (dto.getComboServicioId() != null) {
            entity.setComboServicio(comboServicioRepository.findById(dto.getComboServicioId())
                    .orElseThrow(() -> new Exception("ComboServicio no encontrado con id: " + dto.getComboServicioId())));
        }
        entity.setDuracionEstimadaMinutos(dto.getDuracionEstimadaMinutos());
        entity.setPrecioAcordado(dto.getPrecioAcordado());
        entity.setNotas(dto.getNotas());
    }
}
