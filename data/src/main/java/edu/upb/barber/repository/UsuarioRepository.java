package edu.upb.barber.repository;

import edu.upb.barber.repository.dto.response.UsuarioResponseDto;
import edu.upb.barber.repository.entity.Usuario;
import edu.upb.barber.repository.entity.enums.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Modifying
    @Query("""
            UPDATE Usuario u
            SET u.nombre = :pNombre,
                u.apellido = :pApellido,
                u.email = :pEmail,
                u.rol = :pRol,
                u.activo = :pActivo
            WHERE u.id = :pUsuarioId
            """)
    void actualizarUsuario(
            @Param("pUsuarioId") String usuarioId,
            @Param("pNombre") String nombre,
            @Param("pApellido") String apellido,
            @Param("pEmail") String email,
            @Param("pRol") RolUsuario rol,
            @Param("pActivo") boolean activo
    );

    Optional<Usuario> findByNombreIgnoreCase(String nombre);

    @Query("SELECT u FROM Usuario u WHERE  u.id=:pId")
    Optional<Usuario> findByUserIdToValidateSession(@Param("pId") String pId);
}
