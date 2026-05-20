package edu.upb.springsito.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.upb.springsito.repository.ComboServicioRepository;
import edu.upb.springsito.repository.entity.ComboServicio;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ComboServicioService {

    private final ComboServicioRepository repository;

    @Transactional(propagation = Propagation.REQUIRED)
    public ComboServicio save(ComboServicio entity) {
        return repository.save(entity);
    }

    public List<ComboServicio> listar() {
        return repository.findAll();
    }
}