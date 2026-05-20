package edu.upb.springsito.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.upb.springsito.repository.EmpleadoSucursalRepository;
import edu.upb.springsito.repository.entity.EmpleadoSucursal;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class EmpleadoSucursalService {

    private final EmpleadoSucursalRepository repository;

    @Transactional(propagation = Propagation.REQUIRED)
    public EmpleadoSucursal save(EmpleadoSucursal entity) {
        return repository.save(entity);
    }

    public List<EmpleadoSucursal> listar() {
        return repository.findAll();
    }
}