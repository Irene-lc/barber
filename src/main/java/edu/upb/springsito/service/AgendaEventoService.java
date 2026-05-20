package edu.upb.springsito.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.upb.springsito.repository.AgendaEventoRepository;
import edu.upb.springsito.dto.AgendaEventoDto;
import edu.upb.springsito.repository.entity.AgendaEvento;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AgendaEventoService {

    private final AgendaEventoRepository repository;

    @Transactional(propagation = Propagation.REQUIRED)
    public AgendaEvento save(AgendaEvento entity) {
        return repository.save(entity);
    }

    public List<AgendaEvento> listar() {
        return repository.findAll();
    }

    public List<AgendaEventoDto> listarDto() {
        return repository.listarDto();
    }
}
