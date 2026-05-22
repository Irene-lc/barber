package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicioRepository extends JpaRepository<Servicio, String> {
}
