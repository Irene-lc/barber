package edu.upb.springsito.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.upb.springsito.repository.entity.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, String> {
}
