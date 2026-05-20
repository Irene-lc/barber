package edu.upb.springsito.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.upb.springsito.repository.ProductoRepository;
import edu.upb.springsito.repository.entity.Producto;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ProductoService {

    private final ProductoRepository repository;

    @Transactional(propagation = Propagation.REQUIRED)
    public Producto save(Producto entity) {
        return repository.save(entity);
    }

    public List<Producto> listar() {
        return repository.findAll();
    }
}