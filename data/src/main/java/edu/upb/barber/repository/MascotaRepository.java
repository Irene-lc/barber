package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MascotaRepository extends JpaRepository<Mascota, String> {
}
