package edu.upb.barber.service;

import edu.upb.barber.dto.request.AgendaEventoRequest;
import edu.upb.barber.dto.response.AgendaEventoResponse;
import edu.upb.barber.repository.AgendaEventoRepository;
import edu.upb.barber.repository.ClienteRepository;
import edu.upb.barber.repository.MascotaRepository;
import edu.upb.barber.repository.SucursalRepository;
import edu.upb.barber.repository.UsuarioRepository;
import edu.upb.barber.repository.entity.AgendaEvento;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class AgendaEventoService {
    private final AgendaEventoRepository repository;
    private final ClienteRepository clienteRepository;
    private final MascotaRepository mascotaRepository;
    private final SucursalRepository sucursalRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<AgendaEventoResponse> listar() {
        return repository.findAll().stream().map(AgendaEventoResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public AgendaEventoResponse buscarPorId(String id) throws Exception {
        return repository.findById(id)
                .map(AgendaEventoResponse::fromEntity)
                .orElseThrow(() -> new Exception("AgendaEvento no encontrado con id: " + id));
    }

    @Transactional
    public void guardar(AgendaEventoRequest dto) throws Exception {
        if (dto.getInicio() == null || dto.getFin() == null) {
            throw new Exception("Los campos inicio y fin son requeridos");
        }
        AgendaEvento entity = new AgendaEvento();
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void actualizar(String id, AgendaEventoRequest dto) throws Exception {
        AgendaEvento entity = repository.findById(id)
                .orElseThrow(() -> new Exception("AgendaEvento no encontrado con id: " + id));
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void eliminar(String id) throws Exception {
        if (!repository.existsById(id)) {
            throw new Exception("AgendaEvento no encontrado con id: " + id);
        }
        repository.deleteById(id);
    }

    private void mapearDesdeDto(AgendaEventoRequest dto, AgendaEvento entity) throws Exception {
        if (dto.getClienteId() != null) {
            entity.setCliente(clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new Exception("Cliente no encontrado con id: " + dto.getClienteId())));
        }
        if (dto.getMascotaId() != null) {
            entity.setMascota(mascotaRepository.findById(dto.getMascotaId())
                    .orElseThrow(() -> new Exception("Mascota no encontrada con id: " + dto.getMascotaId())));
        }
        entity.setSucursal(sucursalRepository.findById(dto.getSucursalId())
                .orElseThrow(() -> new Exception("Sucursal no encontrada con id: " + dto.getSucursalId())));
        if (dto.getCreadoPorUsuarioId() != null) {
            entity.setCreadoPorUsuario(usuarioRepository.findById(dto.getCreadoPorUsuarioId())
                    .orElseThrow(() -> new Exception("Usuario no encontrado con id: " + dto.getCreadoPorUsuarioId())));
        }
        entity.setTipoEvento(dto.getTipoEvento());
        entity.setEstado(dto.getEstado());
        entity.setInicio(dto.getInicio());
        entity.setFin(dto.getFin());
        entity.setNotas(dto.getNotas());
    }
}
