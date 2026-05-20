package edu.upb.barber.service;

import edu.upb.barber.dto.request.MascotaRequest;
import edu.upb.barber.dto.response.MascotaResponse;
import edu.upb.barber.repository.ClienteRepository;
import edu.upb.barber.repository.MascotaRepository;
import edu.upb.barber.repository.entity.Mascota;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class MascotaService {
    private final MascotaRepository repository;
    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<MascotaResponse> listar() {
        return repository.findAll().stream().map(MascotaResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public MascotaResponse buscarPorId(String id) throws Exception {
        return repository.findById(id)
                .map(MascotaResponse::fromEntity)
                .orElseThrow(() -> new Exception("Mascota no encontrada con id: " + id));
    }

    @Transactional
    public void guardar(MascotaRequest dto) throws Exception {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new Exception("El campo nombre es requerido");
        }
        Mascota entity = new Mascota();
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void actualizar(String id, MascotaRequest dto) throws Exception {
        Mascota entity = repository.findById(id)
                .orElseThrow(() -> new Exception("Mascota no encontrada con id: " + id));
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void eliminar(String id) throws Exception {
        Mascota entity = repository.findById(id)
                .orElseThrow(() -> new Exception("Mascota no encontrada con id: " + id));
        entity.setActiva(false);
        repository.save(entity);
    }

    private void mapearDesdeDto(MascotaRequest dto, Mascota entity) throws Exception {
        entity.setCliente(clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new Exception("Cliente no encontrado con id: " + dto.getClienteId())));
        entity.setNombre(dto.getNombre());
        entity.setEspecie(dto.getEspecie());
        entity.setRaza(dto.getRaza());
        entity.setNotasEspeciales(dto.getNotasEspeciales());
        entity.setActiva(dto.isActiva());
    }
}
