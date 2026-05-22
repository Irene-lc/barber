package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.Especie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EspecieRepository extends JpaRepository<Especie, String> {

    List<Especie> findByActivoTrueOrderByNombreAsc();

    Optional<Especie> findByNombreIgnoreCase(String nombre);
}