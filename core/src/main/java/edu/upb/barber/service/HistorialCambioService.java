package edu.upb.barber.service;

import edu.upb.barber.dto.request.HistorialCambioRequest;
import edu.upb.barber.dto.response.HistorialCambioResponse;
import edu.upb.barber.repository.AgendaEventoRepository;
import edu.upb.barber.repository.HistorialCambioRepository;
import edu.upb.barber.repository.UsuarioRepository;
import edu.upb.barber.repository.entity.HistorialCambio;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class HistorialCambioService {
    private final HistorialCambioRepository repository;
    private final AgendaEventoRepository agendaEventoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<HistorialCambioResponse> listar() {
        return repository.findAll().stream().map(HistorialCambioResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public HistorialCambioResponse buscarPorId(String id) throws Exception {
        return repository.findById(id)
                .map(HistorialCambioResponse::fromEntity)
                .orElseThrow(() -> new Exception("HistorialCambio no encontrado con id: " + id));
    }

    @Transactional
    public void guardar(HistorialCambioRequest dto) throws Exception {
        if (dto.getCampoModificado() == null || dto.getCampoModificado().isBlank()) {
            throw new Exception("El campo campoModificado es requerido");
        }
        HistorialCambio entity = new HistorialCambio();
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    private void mapearDesdeDto(HistorialCambioRequest dto, HistorialCambio entity) throws Exception {
        entity.setAgendaEvento(agendaEventoRepository.findById(dto.getAgendaEventoId())
                .orElseThrow(() -> new Exception("AgendaEvento no encontrado con id: " + dto.getAgendaEventoId())));
        if (dto.getModificadoPorUsuarioId() != null) {
            entity.setModificadoPorUsuario(usuarioRepository.findById(dto.getModificadoPorUsuarioId())
                    .orElseThrow(() -> new Exception("Usuario no encontrado con id: " + dto.getModificadoPorUsuarioId())));
        }
        entity.setCampoModificado(dto.getCampoModificado());
        entity.setValorAnterior(dto.getValorAnterior());
        entity.setValorNuevo(dto.getValorNuevo());
    }
}
