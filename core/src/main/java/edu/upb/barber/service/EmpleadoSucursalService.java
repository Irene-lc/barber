package edu.upb.barber.service;

import edu.upb.barber.repository.EmpleadoRepository;
import edu.upb.barber.repository.EmpleadoSucursalRepository;
import edu.upb.barber.repository.SucursalRepository;
import edu.upb.barber.repository.dto.request.EmpleadoSucursalRequestDto;
import edu.upb.barber.repository.dto.response.EmpleadoSucursalResponseDto;
import edu.upb.barber.repository.entity.Empleado;
import edu.upb.barber.repository.entity.EmpleadoSucursal;
import edu.upb.barber.repository.entity.Sucursal;
import edu.upb.barber.service.exception.OperationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class EmpleadoSucursalService {

    private final EmpleadoSucursalRepository empleadoSucursalRepository;
    private final EmpleadoRepository empleadoRepository;
    private final SucursalRepository sucursalRepository;
    private final LogService logService;

    @Transactional(readOnly = true)
    public List<EmpleadoSucursalResponseDto> listar() {
        return empleadoSucursalRepository.findAll().stream()
                .map(EmpleadoSucursalResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<EmpleadoSucursalResponseDto> findById(String id) {
        return empleadoSucursalRepository.findById(id)
                .map(EmpleadoSucursalResponseDto::new);
    }

    @Transactional
    public EmpleadoSucursalResponseDto save(EmpleadoSucursalRequestDto dto) throws Exception {
        if (dto.getEmpleadoId() == null || dto.getEmpleadoId().isBlank()) {
            log.error("Error al guardar EmpleadoSucursal. El campo empleado_id es requerido");
            logService.error("Error al guardar EmpleadoSucursal. El campo empleado_id es requerido");
            throw new OperationException("El campo empleado_id es requerido");
        }
        if (dto.getSucursalId() == null || dto.getSucursalId().isBlank()) {
            log.error("Error al guardar EmpleadoSucursal. El campo sucursal_id es requerido");
            logService.error("Error al guardar EmpleadoSucursal. El campo sucursal_id es requerido");
            throw new OperationException("El campo sucursal_id es requerido");
        }

        Empleado empleado = empleadoRepository.findById(dto.getEmpleadoId())
                .orElseThrow(() -> new OperationException("Empleado no encontrado con id: " + dto.getEmpleadoId()));

        Sucursal sucursal = sucursalRepository.findById(dto.getSucursalId())
                .orElseThrow(() -> new OperationException("Sucursal no encontrada con id: " + dto.getSucursalId()));

        EmpleadoSucursal es = new EmpleadoSucursal();
        es.setEmpleado(empleado);
        es.setSucursal(sucursal);
        if (dto.getActivo() != null) {
            es.setActivo(dto.getActivo());
        }

        logService.info("EmpleadoSucursal guardado exitosamente para empleado: " + dto.getEmpleadoId());
        return new EmpleadoSucursalResponseDto(empleadoSucursalRepository.save(es));
    }

    @Transactional
    public EmpleadoSucursalResponseDto update(String id, EmpleadoSucursalRequestDto dto) throws Exception {
        EmpleadoSucursal es = empleadoSucursalRepository.findById(id)
                .orElseThrow(() -> new OperationException("EmpleadoSucursal no encontrado con id: " + id));

        if (dto.getActivo() != null) {
            es.setActivo(dto.getActivo());
        }

        logService.info("EmpleadoSucursal actualizado exitosamente: " + id);
        return new EmpleadoSucursalResponseDto(empleadoSucursalRepository.save(es));
    }

    @Transactional
    public void delete(String id) throws Exception {
        if (!empleadoSucursalRepository.existsById(id)) {
            log.error("Error al eliminar EmpleadoSucursal. No encontrado con id: {}", id);
            logService.error("Error al eliminar EmpleadoSucursal. No encontrado con id: " + id);
            throw new OperationException("EmpleadoSucursal no encontrado con id: " + id);
        }
        empleadoSucursalRepository.deleteById(id);
        logService.info("EmpleadoSucursal eliminado exitosamente: " + id);
    }
}
