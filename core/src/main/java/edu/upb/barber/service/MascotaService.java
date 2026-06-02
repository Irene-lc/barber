package edu.upb.barber.service;

import edu.upb.barber.repository.ClienteRepository;
import edu.upb.barber.repository.MascotaRepository;
import edu.upb.barber.repository.RazaRepository;
import edu.upb.barber.repository.dto.request.MascotaRequestDto;
import edu.upb.barber.repository.dto.response.MascotaResponseDto;
import edu.upb.barber.repository.entity.Cliente;
import edu.upb.barber.repository.entity.Mascota;
import edu.upb.barber.repository.entity.Raza;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MascotaService {

    private final MascotaRepository mascotaRepository;
    private final RazaRepository razaRepository;
    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<MascotaResponseDto> listar() {
        return mascotaRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<MascotaResponseDto> obtenerPorId(String id) {
        return mascotaRepository.findById(id)
                .map(this::mapToResponse);
    }

    @Transactional
    public MascotaResponseDto guardar(MascotaRequestDto dto) throws Exception {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new Exception("El campo nombre es requerido");
        }
        if (dto.getClienteId() == null || dto.getClienteId().isBlank()) {
            throw new Exception("El campo cliente_id es requerido");
        }
        if (dto.getRazaId() == null || dto.getRazaId().isBlank()) {
            throw new Exception("El campo raza_id es requerido");
        }

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new Exception("Cliente no encontrado con id: " + dto.getClienteId()));

        Raza raza = razaRepository.findById(dto.getRazaId())
                .orElseThrow(() -> new Exception("Raza no encontrada con id: " + dto.getRazaId()));

        Mascota mascota = new Mascota();
        mascota.setNombre(dto.getNombre());
        mascota.setCliente(cliente);
        mascota.setRaza(raza);
        if (dto.getActivo() != null) {
            mascota.setActivo(dto.getActivo());
        }

        return mapToResponse(mascotaRepository.save(mascota));
    }

    @Transactional
    public MascotaResponseDto update(String mascotaId, MascotaRequestDto dto) throws Exception {
        Mascota mascota = mascotaRepository.findById(mascotaId)
                .orElseThrow(() -> new Exception("Mascota no encontrada con id: " + mascotaId));

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new Exception("El campo nombre es requerido");
        }

        mascota.setNombre(dto.getNombre());

        if (dto.getClienteId() != null && !dto.getClienteId().isBlank()) {
            Cliente cliente = clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new Exception("Cliente no encontrado con id: " + dto.getClienteId()));
            mascota.setCliente(cliente);
        }

        if (dto.getRazaId() != null && !dto.getRazaId().isBlank()) {
            Raza raza = razaRepository.findById(dto.getRazaId())
                    .orElseThrow(() -> new Exception("Raza no encontrada con id: " + dto.getRazaId()));
            mascota.setRaza(raza);
        }

        if (dto.getActivo() != null) {
            mascota.setActivo(dto.getActivo());
        }

        return mapToResponse(mascotaRepository.save(mascota));
    }

    @Transactional
    public void eliminar(String id) throws Exception {
        if (!mascotaRepository.existsById(id)) {
            throw new Exception("Mascota no encontrada con id: " + id);
        }
        mascotaRepository.deleteById(id);
    }

    private MascotaResponseDto mapToResponse(Mascota mascota) {
        return MascotaResponseDto.builder()
                .id(mascota.getId())
                .nombre(mascota.getNombre())
                .clienteId(mascota.getCliente() != null ? mascota.getCliente().getId() : null)
                .razaId(mascota.getRaza() != null ? mascota.getRaza().getId() : null)
                .activo(mascota.isActivo())
                .build();
    }
}