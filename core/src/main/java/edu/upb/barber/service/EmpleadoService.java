package edu.upb.barber.service;

import edu.upb.barber.dto.request.EmpleadoRequest;
import edu.upb.barber.dto.response.EmpleadoResponse;
import edu.upb.barber.repository.EmpleadoRepository;
import edu.upb.barber.repository.EmpresaRepository;
import edu.upb.barber.repository.UsuarioRepository;
import edu.upb.barber.repository.entity.Empleado;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class EmpleadoService {
    private final EmpleadoRepository repository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<EmpleadoResponse> listar() {
        return repository.findAll().stream().map(EmpleadoResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public EmpleadoResponse buscarPorId(String id) throws Exception {
        return repository.findById(id)
                .map(EmpleadoResponse::fromEntity)
                .orElseThrow(() -> new Exception("Empleado no encontrado con id: " + id));
    }

    @Transactional
    public void guardar(EmpleadoRequest dto) throws Exception {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new Exception("El campo nombre es requerido");
        }
        Empleado entity = new Empleado();
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void actualizar(String id, EmpleadoRequest dto) throws Exception {
        Empleado entity = repository.findById(id)
                .orElseThrow(() -> new Exception("Empleado no encontrado con id: " + id));
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void eliminar(String id) throws Exception {
        Empleado entity = repository.findById(id)
                .orElseThrow(() -> new Exception("Empleado no encontrado con id: " + id));
        entity.setActivo(false);
        repository.save(entity);
    }

    private void mapearDesdeDto(EmpleadoRequest dto, Empleado entity) throws Exception {
        entity.setEmpresa(empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new Exception("Empresa no encontrada con id: " + dto.getEmpresaId())));
        if (dto.getUsuarioId() != null) {
            entity.setUsuario(usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new Exception("Usuario no encontrado con id: " + dto.getUsuarioId())));
        }
        entity.setNombre(dto.getNombre());
        entity.setTelefono(dto.getTelefono());
        entity.setEmail(dto.getEmail());
        entity.setCargo(dto.getCargo());
        entity.setEspecialidad(dto.getEspecialidad());
        entity.setPorcentajeComision(dto.getPorcentajeComision());
        entity.setPagoFijoMensual(dto.getPagoFijoMensual());
        entity.setFotoUrl(dto.getFotoUrl());
        entity.setDisponible(dto.isDisponible());
        entity.setActivo(dto.isActivo());
    }
}
