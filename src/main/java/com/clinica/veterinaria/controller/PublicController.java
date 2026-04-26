package com.clinica.veterinaria.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicController {

    @GetMapping("/")
    public String landing() {
        return "public/landing";
    }

    @GetMapping("/public/pedir-cita")
    public String pedirCita() {
        return "redirect:/cliente/citas/nueva";
    }

    @GetMapping("/public/mis-datos")
    public String misDatos() {
        return "redirect:/cliente/historial";
    }
}
