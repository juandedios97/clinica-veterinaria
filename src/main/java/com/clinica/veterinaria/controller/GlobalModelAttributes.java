package com.clinica.veterinaria.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute
    public void addCurrentPath(HttpServletRequest request, Model model) {
        model.addAttribute("currentPath", request.getRequestURI());
    }
}