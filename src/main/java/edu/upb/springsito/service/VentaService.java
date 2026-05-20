package edu.upb.springsito.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.upb.springsito.repository.VentaRepository;
import edu.upb.springsito.repository.entity.Venta;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class VentaService {

    private final VentaRepository repository;

    @Transactional(propagation = Propagation.REQUIRED)
    public Venta save(Venta entity) {
        return repository.save(entity);
    }

    public List<Venta> listar() {
        return repository.findAll();
    }
}