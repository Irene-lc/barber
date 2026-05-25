package edu.upb.barber.repository;

import edu.upb.barber.repository.dto.response.EmpresaResponseDto;
import edu.upb.barber.repository.entity.Empresa;
import edu.upb.barber.repository.entity.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MateriaRepository extends JpaRepository<Materia, String> {

    Optional<Materia> findBySigla(String sigla);

    List<Materia> findByNombre(String nombre);

//    @Query("""
//            SELECT new edu.upb.barber.repository.dto.response.EmpresaResponseDto(
//                e.id,
//                e.nombre,
//                e.nit
//            )
//            FROM Empresa e
//            WHERE e.nombre = :pNombre
//            """)
//    List<Empresa> findByNombreAux(
//            @Param("pNombre") String nombre
//    );



    @Modifying
    @Query("UPDATE Materia e SET e.nombre=:pNombre, e.sigla=:pSigla WHERE e.id=:pMateriaId")
    void actualizarMateria(
            @Param("pMateriaId") String pMateriaId,
            @Param("pNombre") String nombre,
            @Param("pSigla") String sigla);
}
