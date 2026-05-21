package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.HorarioEmpleado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HorarioEmpleadoRepository extends JpaRepository<HorarioEmpleado, String> {
}
