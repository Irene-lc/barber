package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, String> {
    @Query("SELECT e FROM Estudiante e")
    List<Estudiante> listar();
    @Modifying
    @Query("UPDATE Estudiante e SET e.nroTelefono=:pNroTelefono, e.nroDocumento=:pNroDocumento WHERE e.id=:pEstudianteId")
    void actualizarEstudiante(
            @Param("pEstudianteId")String estudianteId,
            @Param("pNroTelefono")String telefono,
            @Param("nroDocumento")String documento);
}