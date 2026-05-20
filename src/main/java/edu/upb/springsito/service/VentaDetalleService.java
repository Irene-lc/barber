package edu.upb.springsito.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.upb.springsito.repository.VentaDetalleRepository;
import edu.upb.springsito.repository.entity.VentaDetalle;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class VentaDetalleService {

    private final VentaDetalleRepository repository;

    @Transactional(propagation = Propagation.REQUIRED)
    public VentaDetalle save(VentaDetalle entity) {
        return repository.save(entity);
    }

    public List<VentaDetalle> listar() {
        return repository.findAll();
    }
}