package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, String> {
}
