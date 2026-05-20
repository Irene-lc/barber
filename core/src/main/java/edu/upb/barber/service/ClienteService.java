package edu.upb.barber.service;

import edu.upb.barber.dto.request.ClienteRequest;
import edu.upb.barber.dto.response.ClienteResponse;
import edu.upb.barber.repository.ClienteRepository;
import edu.upb.barber.repository.EmpresaRepository;
import edu.upb.barber.repository.UsuarioRepository;
import edu.upb.barber.repository.entity.Cliente;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ClienteService {
    private final ClienteRepository repository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return repository.findAll().stream().map(ClienteResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(String id) throws Exception {
        return repository.findById(id)
                .map(ClienteResponse::fromEntity)
                .orElseThrow(() -> new Exception("Cliente no encontrado con id: " + id));
    }

    @Transactional
    public void guardar(ClienteRequest dto) throws Exception {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new Exception("El campo nombre es requerido");
        }
        Cliente entity = new Cliente();
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void actualizar(String id, ClienteRequest dto) throws Exception {
        Cliente entity = repository.findById(id)
                .orElseThrow(() -> new Exception("Cliente no encontrado con id: " + id));
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void eliminar(String id) throws Exception {
        Cliente entity = repository.findById(id)
                .orElseThrow(() -> new Exception("Cliente no encontrado con id: " + id));
        entity.setActivo(false);
        repository.save(entity);
    }

    private void mapearDesdeDto(ClienteRequest dto, Cliente entity) throws Exception {
        entity.setEmpresa(empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new Exception("Empresa no encontrada con id: " + dto.getEmpresaId())));
        if (dto.getUsuarioId() != null) {
            entity.setUsuario(usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new Exception("Usuario no encontrado con id: " + dto.getUsuarioId())));
        }
        entity.setNombre(dto.getNombre());
        entity.setTelefono(dto.getTelefono());
        entity.setEmail(dto.getEmail());
        entity.setDocumento(dto.getDocumento());
        entity.setNotas(dto.getNotas());
        entity.setActivo(dto.isActivo());
    }
}
