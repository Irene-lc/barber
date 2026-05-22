package edu.upb.barber.repository;

import edu.upb.barber.repository.dto.response.UsuarioResponseDto;
import edu.upb.barber.repository.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByNombre(String nombre);

    @Query("""
            SELECT new edu.upb.barber.repository.dto.response.UsuarioResponseDto(
                u.id,
                u.nombre,
                u.email
            )
            FROM Usuario u
            WHERE u.nombre = :pNombre
            """)
    List<UsuarioResponseDto> findByNombreAux(@Param("pNombre") String nombre);

}