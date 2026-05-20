package edu.upb.barber.service;

import edu.upb.barber.dto.request.PagoRequest;
import edu.upb.barber.dto.response.PagoResponse;
import edu.upb.barber.repository.PagoRepository;
import edu.upb.barber.repository.UsuarioRepository;
import edu.upb.barber.repository.VentaRepository;
import edu.upb.barber.repository.entity.Pago;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class PagoService {
    private final PagoRepository repository;
    private final VentaRepository ventaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<PagoResponse> listar() {
        return repository.findAll().stream().map(PagoResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public PagoResponse buscarPorId(String id) throws Exception {
        return repository.findById(id)
                .map(PagoResponse::fromEntity)
                .orElseThrow(() -> new Exception("Pago no encontrado con id: " + id));
    }

    @Transactional
    public void guardar(PagoRequest dto) throws Exception {
        if (dto.getMonto() == null) {
            throw new Exception("El campo monto es requerido");
        }
        if (dto.getMetodoPago() == null) {
            throw new Exception("El campo metodoPago es requerido");
        }
        Pago entity = new Pago();
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void actualizar(String id, PagoRequest dto) throws Exception {
        Pago entity = repository.findById(id)
                .orElseThrow(() -> new Exception("Pago no encontrado con id: " + id));
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void eliminar(String id) throws Exception {
        if (!repository.existsById(id)) {
            throw new Exception("Pago no encontrado con id: " + id);
        }
        repository.deleteById(id);
    }

    private void mapearDesdeDto(PagoRequest dto, Pago entity) throws Exception {
        entity.setVenta(ventaRepository.findById(dto.getVentaId())
                .orElseThrow(() -> new Exception("Venta no encontrada con id: " + dto.getVentaId())));
        entity.setMonto(dto.getMonto());
        entity.setMetodoPago(dto.getMetodoPago());
        entity.setEstadoPago(dto.getEstadoPago());
        entity.setPagadoEn(dto.getPagadoEn());
        if (dto.getRegistradoPorUsuarioId() != null) {
            entity.setRegistradoPorUsuario(usuarioRepository.findById(dto.getRegistradoPorUsuarioId())
                    .orElseThrow(() -> new Exception("Usuario no encontrado con id: " + dto.getRegistradoPorUsuarioId())));
        }
    }
}
