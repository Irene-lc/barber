package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, String> {

    @Query("SELECT m FROM Materia m")
    List<Materia> listar();

    @Modifying
    @Query("UPDATE Materia m SET m.nombre=:pNombre, m.sigla=:pSigla WHERE m.id=:pMateriaId")
    void actualizarMateria(
            @Param("pMateriaId")String materiaId,
            @Param("pNombre")String nombre,
            @Param("pSigla")String sigla);
}