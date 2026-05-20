package edu.upb.barber.service;

import edu.upb.barber.dto.request.ComisionRequest;
import edu.upb.barber.dto.response.ComisionResponse;
import edu.upb.barber.repository.ComisionRepository;
import edu.upb.barber.repository.EmpleadoRepository;
import edu.upb.barber.repository.VentaDetalleRepository;
import edu.upb.barber.repository.entity.Comision;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ComisionService {
    private final ComisionRepository repository;
    private final VentaDetalleRepository ventaDetalleRepository;
    private final EmpleadoRepository empleadoRepository;

    @Transactional(readOnly = true)
    public List<ComisionResponse> listar() {
        return repository.findAll().stream().map(ComisionResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public ComisionResponse buscarPorId(String id) throws Exception {
        return repository.findById(id)
                .map(ComisionResponse::fromEntity)
                .orElseThrow(() -> new Exception("Comision no encontrada con id: " + id));
    }

    @Transactional
    public void guardar(ComisionRequest dto) throws Exception {
        if (dto.getMontoComision() == null) {
            throw new Exception("El campo montoComision es requerido");
        }
        Comision entity = new Comision();
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void actualizar(String id, ComisionRequest dto) throws Exception {
        Comision entity = repository.findById(id)
                .orElseThrow(() -> new Exception("Comision no encontrada con id: " + id));
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void eliminar(String id) throws Exception {
        if (!repository.existsById(id)) {
            throw new Exception("Comision no encontrada con id: " + id);
        }
        repository.deleteById(id);
    }

    private void mapearDesdeDto(ComisionRequest dto, Comision entity) throws Exception {
        entity.setVentaDetalle(ventaDetalleRepository.findById(dto.getVentaDetalleId())
                .orElseThrow(() -> new Exception("VentaDetalle no encontrado con id: " + dto.getVentaDetalleId())));
        entity.setEmpleado(empleadoRepository.findById(dto.getEmpleadoId())
                .orElseThrow(() -> new Exception("Empleado no encontrado con id: " + dto.getEmpleadoId())));
        entity.setPorcentajeAplicado(dto.getPorcentajeAplicado());
        entity.setMontoComision(dto.getMontoComision());
        entity.setLiquidada(dto.isLiquidada());
        entity.setLiquidadaEn(dto.getLiquidadaEn());
    }
}
