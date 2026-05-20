package edu.upb.springsito.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.upb.springsito.repository.ServicioRepository;
import edu.upb.springsito.repository.entity.Servicio;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ServicioService {

    private final ServicioRepository repository;

    @Transactional(propagation = Propagation.REQUIRED)
    public Servicio save(Servicio entity) {
        return repository.save(entity);
    }

    public List<Servicio> listar() {
        return repository.findAll();
    }
}