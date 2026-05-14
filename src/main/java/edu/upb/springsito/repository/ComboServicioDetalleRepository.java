package edu.upb.springsito.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.upb.springsito.repository.entity.ComboServicioDetalle;

@Repository
public interface ComboServicioDetalleRepository extends JpaRepository<ComboServicioDetalle, String> {
}
