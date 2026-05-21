package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.Comision;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComisionRepository extends JpaRepository<Comision, String> {
}
