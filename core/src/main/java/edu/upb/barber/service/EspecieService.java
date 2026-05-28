
package edu.upb.barber.service;

import edu.upb.barber.repository.EspecieRepository;
import edu.upb.barber.repository.entity.Especie;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EspecieService {

    private final EspecieRepository especieRepository;

    public List<Especie> listar() {
        return especieRepository.findAll();
    }

    public Optional<Especie> obtenerPorId(String id) {
        return especieRepository.findById(id);
    }

    public Especie guardar(Especie especie) {
        return especieRepository.save(especie);
    }

    public void eliminar(String id) {
        especieRepository.deleteById(id);
    }
}