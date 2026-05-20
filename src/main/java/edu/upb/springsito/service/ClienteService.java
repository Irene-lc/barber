package edu.upb.springsito.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.upb.springsito.repository.ClienteRepository;
import edu.upb.springsito.repository.entity.Cliente;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ClienteService {

    private final ClienteRepository repository;

    @Transactional(propagation = Propagation.REQUIRED)
    public Cliente save(Cliente entity) {
        return repository.save(entity);
    }

    public List<Cliente> listar() {
        return repository.findAll();
    }
}