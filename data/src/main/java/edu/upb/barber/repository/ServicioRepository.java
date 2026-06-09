package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServicioRepository extends JpaRepository<Servicio, String> {

    Optional<Servicio> findByEmpresaIdAndNombreIgnoreCase(String empresaId, String nombre);
}
