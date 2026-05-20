package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepository extends JpaRepository<Pago, String> {
}
