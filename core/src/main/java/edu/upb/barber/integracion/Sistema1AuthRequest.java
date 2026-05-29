package edu.upb.barber.integracion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class Sistema1AuthRequest {
    private String nombre;
    private String password;
}
