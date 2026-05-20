package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SucursalRepository extends JpaRepository<Sucursal, String> {
}
