package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleadoRepository extends JpaRepository<Empleado, String> {
}
