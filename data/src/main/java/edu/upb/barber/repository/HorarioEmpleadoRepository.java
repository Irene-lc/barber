package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.HorarioEmpleado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HorarioEmpleadoRepository extends JpaRepository<HorarioEmpleado, String> {
    List<HorarioEmpleado> findByEmpleadoIdAndSucursalIdAndActivoTrue(String empleadoId, String sucursalId);
}
