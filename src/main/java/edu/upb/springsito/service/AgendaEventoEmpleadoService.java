package edu.upb.springsito.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import edu.upb.springsito.repository.AgendaEventoEmpleadoRepository;
import edu.upb.springsito.repository.entity.Empleado;
import edu.upb.springsito.repository.entity.AgendaEventoEmpleado;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AgendaEventoEmpleadoService {

    private final AgendaEventoEmpleadoRepository repository;
    private final EmpleadoService empleadoService;

    @Transactional(propagation = Propagation.REQUIRED)
    public AgendaEventoEmpleado save(AgendaEventoEmpleado entity) {
        if (entity.getEmpleado() == null || entity.getEmpleado().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe enviar empleado.id");
        }
        Empleado empleado = empleadoService.findById(entity.getEmpleado().getId());
        entity.setEmpleado(empleado);
        return repository.save(entity);
    }

    public List<AgendaEventoEmpleado> listar() {
        return repository.findAll();
    }
}
