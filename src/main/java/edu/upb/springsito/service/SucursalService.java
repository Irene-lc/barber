package edu.upb.springsito.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.upb.springsito.repository.SucursalRepository;
import edu.upb.springsito.repository.entity.Sucursal;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class SucursalService {

    private final SucursalRepository repository;

    @Transactional(propagation = Propagation.REQUIRED)
    public Sucursal save(Sucursal entity) {
        return repository.save(entity);
    }

    public List<Sucursal> listar() {
        return repository.findAll();
    }
}