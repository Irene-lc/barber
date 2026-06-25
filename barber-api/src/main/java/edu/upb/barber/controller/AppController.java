package edu.upb.barber.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController {

    @GetMapping(value = {
            "/",
            "/{path:^(?!api$|swagger-ui$|v3$|actuator$|error$)[^\\.]*}",
            "/**/{path:^(?!api$|swagger-ui$|v3$|actuator$|error$)[^\\.]*}"
    })
    public String forwardToFrontend() {
        return "forward:/index.html";
    }
}
