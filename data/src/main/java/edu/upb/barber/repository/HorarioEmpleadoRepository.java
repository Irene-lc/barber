package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.HorarioEmpleado;
import edu.upb.barber.repository.entity.enums.DiaSemana;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HorarioEmpleadoRepository extends JpaRepository<HorarioEmpleado, String> {

    List<HorarioEmpleado> findBySucursalIdAndDiaSemanaAndActivoTrue(String sucursalId, DiaSemana diaSemana);

    Optional<HorarioEmpleado> findByEmpleadoIdAndSucursalIdAndDiaSemana(
            String empleadoId,
            String sucursalId,
            DiaSemana diaSemana
    );
}
