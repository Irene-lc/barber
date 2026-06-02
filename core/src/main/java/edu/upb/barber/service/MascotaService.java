
package edu.upb.barber.service;

import edu.upb.barber.repository.MascotaRepository;
import edu.upb.barber.repository.RazaRepository;
import edu.upb.barber.repository.dto.request.MascotaRequestDto;
import edu.upb.barber.repository.entity.Mascota;
import edu.upb.barber.repository.entity.Raza;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MascotaService {
    private final MascotaRepository mascotaRepository;
    private final RazaRepository razaRepository;

    @Transactional
    public List<Mascota> listar() {
        return mascotaRepository.findAll();
    }

    @Transactional
    public Optional<Mascota> obtenerPorId(String id) {
        return mascotaRepository.findById(id);
    }

    @Transactional
    public Mascota guardar(Mascota mascota) {
        return mascotaRepository.save(mascota);
    }

    @Transactional
    public Mascota update(String mascotaId, MascotaRequestDto dto) throws Exception {

        Mascota mascota = mascotaRepository.findById(mascotaId)
                .orElseThrow(() -> new Exception("Mascota no encontrada con id: " + mascotaId));

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new Exception("El campo nombre es requerido");
        }

        mascota.setNombre(dto.getNombre());

        if (dto.getActivo() != null) {
            mascota.setActivo(dto.getActivo());
        }

        if (dto.getRazaId() != null && !dto.getRazaId().isBlank()) {
            Raza raza = razaRepository.findById(dto.getRazaId())
                    .orElseThrow(() -> new Exception("Raza no encontrada con id: " + dto.getRazaId()));
            mascota.setRaza(raza);
        }

        return mascotaRepository.save(mascota);
    }

    @Transactional
    public void eliminar(String id) {
        mascotaRepository.deleteById(id);
    }
}