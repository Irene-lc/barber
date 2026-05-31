package edu.upb.barber.controller;

import edu.upb.barber.repository.entity.Mascota;
import edu.upb.barber.service.MascotaService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mascotas")
@AllArgsConstructor
public class MascotaController {

    private final MascotaService mascotaService;

    @GetMapping
    public List<Mascota> listar() {
        return mascotaService.listar();
    }

    @PostMapping
    public Mascota guardar(
            @RequestBody Mascota mascota
    ) {
        return mascotaService.guardar(mascota);
    }
}