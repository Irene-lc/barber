package edu.upb.springsito.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.upb.springsito.repository.ComboServicioDetalleRepository;
import edu.upb.springsito.repository.entity.ComboServicioDetalle;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ComboServicioDetalleService {

    private final ComboServicioDetalleRepository repository;

    @Transactional(propagation = Propagation.REQUIRED)
    public ComboServicioDetalle save(ComboServicioDetalle entity) {
        return repository.save(entity);
    }

    public List<ComboServicioDetalle> listar() {
        return repository.findAll();
    }
}