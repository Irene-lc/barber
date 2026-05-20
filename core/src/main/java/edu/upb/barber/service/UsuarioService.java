package edu.upb.barber.service;

import edu.upb.barber.repository.dto.request.UsuarioRequest;
import edu.upb.barber.repository.dto.response.UsuarioResponse;
import edu.upb.barber.repository.EmpresaRepository;
import edu.upb.barber.repository.UsuarioRepository;
import edu.upb.barber.repository.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UsuarioService {
    private final UsuarioRepository repository;
    private final EmpresaRepository empresaRepository;

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return repository.findAll().stream().map(UsuarioResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(String id) throws Exception {
        return repository.findById(id)
                .map(UsuarioResponse::fromEntity)
                .orElseThrow(() -> new Exception("Usuario no encontrado con id: " + id));
    }

    @Transactional
    public void guardar(UsuarioRequest dto) throws Exception {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new Exception("El campo nombre es requerido");
        }
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new Exception("El campo email es requerido");
        }
        Usuario entity = new Usuario();
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void actualizar(String id, UsuarioRequest dto) throws Exception {
        Usuario entity = repository.findById(id)
                .orElseThrow(() -> new Exception("Usuario no encontrado con id: " + id));
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void eliminar(String id) throws Exception {
        Usuario entity = repository.findById(id)
                .orElseThrow(() -> new Exception("Usuario no encontrado con id: " + id));
        entity.setActivo(false);
        repository.save(entity);
    }

    private void mapearDesdeDto(UsuarioRequest dto, Usuario entity) throws Exception {
        if (dto.getEmpresaId() != null) {
            entity.setEmpresa(empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new Exception("Empresa no encontrada con id: " + dto.getEmpresaId())));
        }
        entity.setNombre(dto.getNombre());
        entity.setApellido(dto.getApellido());
        entity.setEmail(dto.getEmail());
        entity.setPasswordHash(dto.getPasswordHash());
        entity.setRol(dto.getRol());
        entity.setActivo(dto.isActivo());
    }
}
