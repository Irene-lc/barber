package edu.upb.springsito.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.upb.springsito.repository.AgendaEventoDetalleRepository;
import edu.upb.springsito.repository.entity.AgendaEventoDetalle;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AgendaEventoDetalleService {

    private final AgendaEventoDetalleRepository repository;

    @Transactional(propagation = Propagation.REQUIRED)
    public AgendaEventoDetalle save(AgendaEventoDetalle entity) {
        return repository.save(entity);
    }

    public List<AgendaEventoDetalle> listar() {
        return repository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AgendaEventoDetalle buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("No se encontró el detalle del evento con ID: " + id));
    }

}