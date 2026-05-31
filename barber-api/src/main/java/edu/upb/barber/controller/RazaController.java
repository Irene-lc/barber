package edu.upb.barber.controller;

import edu.upb.barber.repository.entity.Raza;
import edu.upb.barber.service.RazaService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/razas")
@AllArgsConstructor
public class RazaController {

    private final RazaService razaService;

    @GetMapping
    public List<Raza> listar() {
        return razaService.listar();
    }

    @PostMapping
    public Raza guardar(
            @RequestBody Raza raza
    ) {
        return razaService.guardar(raza);
    }
}