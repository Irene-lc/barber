package edu.upb.barber.repository.dto.response;

import edu.upb.barber.repository.entity.Materia;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MateriaResponseDto {
    private String id;
    private String nombre;
    private String sigla;

    public MateriaResponseDto(Materia materia) {
        this.id = materia.getId();
        this.nombre = materia.getNombre();
        this.sigla = materia.getSigla();
    }

    public MateriaResponseDto(
            String id,
            String nombre,
            String sigla
    ) {
        this.id = id;
        this.nombre = nombre;
        this.sigla = sigla;
    }
}
