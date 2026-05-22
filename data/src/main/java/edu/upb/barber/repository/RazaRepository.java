package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.Raza;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RazaRepository extends JpaRepository<Raza, String> {

    List<Raza> findByEspecieIdAndActivoTrueOrderByNombreAsc(String especieId);

    Optional<Raza> findByEspecieIdAndNombreIgnoreCase(String especieId, String nombre);
}