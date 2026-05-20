package edu.upb.barber.service;

import edu.upb.barber.dto.request.VentaRequest;
import edu.upb.barber.dto.response.VentaResponse;
import edu.upb.barber.repository.AgendaEventoRepository;
import edu.upb.barber.repository.ClienteRepository;
import edu.upb.barber.repository.SucursalRepository;
import edu.upb.barber.repository.UsuarioRepository;
import edu.upb.barber.repository.VentaRepository;
import edu.upb.barber.repository.entity.Venta;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class VentaService {
    private final VentaRepository repository;
    private final SucursalRepository sucursalRepository;
    private final ClienteRepository clienteRepository;
    private final AgendaEventoRepository agendaEventoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<VentaResponse> listar() {
        return repository.findAll().stream().map(VentaResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public VentaResponse buscarPorId(String id) throws Exception {
        return repository.findById(id)
                .map(VentaResponse::fromEntity)
                .orElseThrow(() -> new Exception("Venta no encontrada con id: " + id));
    }

    @Transactional
    public void guardar(VentaRequest dto) throws Exception {
        if (dto.getSucursalId() == null || dto.getSucursalId().isBlank()) {
            throw new Exception("El campo sucursalId es requerido");
        }
        Venta entity = new Venta();
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void actualizar(String id, VentaRequest dto) throws Exception {
        Venta entity = repository.findById(id)
                .orElseThrow(() -> new Exception("Venta no encontrada con id: " + id));
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void eliminar(String id) throws Exception {
        if (!repository.existsById(id)) {
            throw new Exception("Venta no encontrada con id: " + id);
        }
        repository.deleteById(id);
    }

    private void mapearDesdeDto(VentaRequest dto, Venta entity) throws Exception {
        entity.setSucursal(sucursalRepository.findById(dto.getSucursalId())
                .orElseThrow(() -> new Exception("Sucursal no encontrada con id: " + dto.getSucursalId())));
        if (dto.getClienteId() != null) {
            entity.setCliente(clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new Exception("Cliente no encontrado con id: " + dto.getClienteId())));
        }
        if (dto.getAgendaEventoId() != null) {
            entity.setAgendaEvento(agendaEventoRepository.findById(dto.getAgendaEventoId())
                    .orElseThrow(() -> new Exception("AgendaEvento no encontrado con id: " + dto.getAgendaEventoId())));
        }
        if (dto.getRegistradoPorUsuarioId() != null) {
            entity.setRegistradoPorUsuario(usuarioRepository.findById(dto.getRegistradoPorUsuarioId())
                    .orElseThrow(() -> new Exception("Usuario no encontrado con id: " + dto.getRegistradoPorUsuarioId())));
        }
        entity.setSubtotal(dto.getSubtotal());
        entity.setDescuento(dto.getDescuento());
        entity.setTotal(dto.getTotal());
        entity.setEstado(dto.getEstado());
        entity.setNotas(dto.getNotas());
    }
}
