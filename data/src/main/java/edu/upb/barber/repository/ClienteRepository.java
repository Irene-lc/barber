package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, String> {
}
