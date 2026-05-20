package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<Venta, String> {
}
