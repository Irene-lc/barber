
package edu.upb.barber.service;

import edu.upb.barber.repository.MascotaRepository;
import edu.upb.barber.repository.entity.Mascota;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MascotaService {

    private final MascotaRepository mascotaRepository;

    public List<Mascota> listar() {
        return mascotaRepository.findAll();
    }

    public Optional<Mascota> obtenerPorId(String id) {
        return mascotaRepository.findById(id);
    }

    public Mascota guardar(Mascota mascota) {
        return mascotaRepository.save(mascota);
    }

    public void eliminar(String id) {
        mascotaRepository.deleteById(id);
    }
}