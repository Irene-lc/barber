package edu.upb.barber.controller;

import edu.upb.barber.repository.entity.Especie;
import edu.upb.barber.service.EspecieService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/especies")
@AllArgsConstructor
public class EspecieController {

    private final EspecieService especieService;

    @GetMapping
    public List<Especie> listar() {
        return especieService.listar();
    }

    @PostMapping
    public Especie guardar(
            @RequestBody Especie especie
    ) {
        return especieService.guardar(especie);
    }
}