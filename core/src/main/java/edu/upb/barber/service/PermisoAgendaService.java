package edu.upb.barber.service;

import edu.upb.barber.repository.dto.request.PermisoAgendaRequest;
import edu.upb.barber.repository.dto.response.PermisoAgendaResponse;
import edu.upb.barber.repository.EmpleadoRepository;
import edu.upb.barber.repository.PermisoAgendaRepository;
import edu.upb.barber.repository.SucursalRepository;
import edu.upb.barber.repository.UsuarioRepository;
import edu.upb.barber.repository.entity.PermisoAgenda;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class PermisoAgendaService {
    private final PermisoAgendaRepository repository;
    private final EmpleadoRepository empleadoRepository;
    private final SucursalRepository sucursalRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<PermisoAgendaResponse> listar() {
        return repository.findAll().stream().map(PermisoAgendaResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public PermisoAgendaResponse buscarPorId(String id) throws Exception {
        return repository.findById(id)
                .map(PermisoAgendaResponse::fromEntity)
                .orElseThrow(() -> new Exception("PermisoAgenda no encontrado con id: " + id));
    }

    @Transactional
    public void guardar(PermisoAgendaRequest dto) throws Exception {
        if (dto.getFechaDesde() == null || dto.getFechaHasta() == null) {
            throw new Exception("Los campos fechaDesde y fechaHasta son requeridos");
        }
        PermisoAgenda entity = new PermisoAgenda();
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void actualizar(String id, PermisoAgendaRequest dto) throws Exception {
        PermisoAgenda entity = repository.findById(id)
                .orElseThrow(() -> new Exception("PermisoAgenda no encontrado con id: " + id));
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void eliminar(String id) throws Exception {
        PermisoAgenda entity = repository.findById(id)
                .orElseThrow(() -> new Exception("PermisoAgenda no encontrado con id: " + id));
        entity.setActivo(false);
        repository.save(entity);
    }

    private void mapearDesdeDto(PermisoAgendaRequest dto, PermisoAgenda entity) throws Exception {
        entity.setEmpleado(empleadoRepository.findById(dto.getEmpleadoId())
                .orElseThrow(() -> new Exception("Empleado no encontrado con id: " + dto.getEmpleadoId())));
        if (dto.getSucursalId() != null) {
            entity.setSucursal(sucursalRepository.findById(dto.getSucursalId())
                    .orElseThrow(() -> new Exception("Sucursal no encontrada con id: " + dto.getSucursalId())));
        }
        if (dto.getOtorgadoPorUsuarioId() != null) {
            entity.setOtorgadoPorUsuario(usuarioRepository.findById(dto.getOtorgadoPorUsuarioId())
                    .orElseThrow(() -> new Exception("Usuario no encontrado con id: " + dto.getOtorgadoPorUsuarioId())));
        }
        entity.setTipoPermiso(dto.getTipoPermiso());
        entity.setFechaDesde(dto.getFechaDesde());
        entity.setFechaHasta(dto.getFechaHasta());
        entity.setHoraInicio(dto.getHoraInicio());
        entity.setHoraFin(dto.getHoraFin());
        entity.setMotivo(dto.getMotivo());
        entity.setActivo(dto.isActivo());
    }
}
