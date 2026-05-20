package edu.upb.barber.service;

import edu.upb.barber.repository.dto.request.AgendaEventoEmpleadoRequest;
import edu.upb.barber.repository.dto.response.AgendaEventoEmpleadoResponse;
import edu.upb.barber.repository.AgendaEventoEmpleadoRepository;
import edu.upb.barber.repository.AgendaEventoRepository;
import edu.upb.barber.repository.EmpleadoRepository;
import edu.upb.barber.repository.entity.AgendaEventoEmpleado;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class AgendaEventoEmpleadoService {
    private final AgendaEventoEmpleadoRepository repository;
    private final AgendaEventoRepository agendaEventoRepository;
    private final EmpleadoRepository empleadoRepository;

    @Transactional(readOnly = true)
    public List<AgendaEventoEmpleadoResponse> listar() {
        return repository.findAll().stream().map(AgendaEventoEmpleadoResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public AgendaEventoEmpleadoResponse buscarPorId(String id) throws Exception {
        return repository.findById(id)
                .map(AgendaEventoEmpleadoResponse::fromEntity)
                .orElseThrow(() -> new Exception("AgendaEventoEmpleado no encontrado con id: " + id));
    }

    @Transactional
    public void guardar(AgendaEventoEmpleadoRequest dto) throws Exception {
        AgendaEventoEmpleado entity = new AgendaEventoEmpleado();
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void actualizar(String id, AgendaEventoEmpleadoRequest dto) throws Exception {
        AgendaEventoEmpleado entity = repository.findById(id)
                .orElseThrow(() -> new Exception("AgendaEventoEmpleado no encontrado con id: " + id));
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void eliminar(String id) throws Exception {
        if (!repository.existsById(id)) {
            throw new Exception("AgendaEventoEmpleado no encontrado con id: " + id);
        }
        repository.deleteById(id);
    }

    private void mapearDesdeDto(AgendaEventoEmpleadoRequest dto, AgendaEventoEmpleado entity) throws Exception {
        entity.setAgendaEvento(agendaEventoRepository.findById(dto.getAgendaEventoId())
                .orElseThrow(() -> new Exception("AgendaEvento no encontrado con id: " + dto.getAgendaEventoId())));
        entity.setEmpleado(empleadoRepository.findById(dto.getEmpleadoId())
                .orElseThrow(() -> new Exception("Empleado no encontrado con id: " + dto.getEmpleadoId())));
        entity.setRolEnEvento(dto.getRolEnEvento());
    }
}
