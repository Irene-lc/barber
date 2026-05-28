package edu.upb.barber.service;

import edu.upb.barber.repository.RazaRepository;
import edu.upb.barber.repository.entity.Raza;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RazaService {

    private final RazaRepository razaRepository;

    public List<Raza> listar() {
        return razaRepository.findAll();
    }

    public Optional<Raza> obtenerPorId(String id) {
        return razaRepository.findById(id);
    }

    public Raza guardar(Raza raza) {
        return razaRepository.save(raza);
    }

    public void eliminar(String id) {
        razaRepository.deleteById(id);
    }
}