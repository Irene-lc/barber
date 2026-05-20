package edu.upb.springsito.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import edu.upb.springsito.repository.EmpleadoRepository;
import edu.upb.springsito.repository.entity.Empleado;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class EmpleadoService {

    private final EmpleadoRepository repository;

    @Transactional(propagation = Propagation.REQUIRED)
    public Empleado save(Empleado entity) {
        return repository.save(entity);
    }

    public List<Empleado> listar() {
        return repository.findAll();
    }

    public Empleado findById(String id) {
        if (id == null || id.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El id de empleado es obligatorio");
        }
        try {
            UUID.fromString(id);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El id de empleado no es un UUID valido");
        }
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado"));
    }
}
