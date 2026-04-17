package com.clinica.veterinaria.dto;

import java.util.List;

public class SearchResponseDTO {
    private List<SearchResultDTO> mascotas;
    private List<SearchResultDTO> propietarios;
    private List<SearchResultDTO> historiales;

    public SearchResponseDTO() {
    }

    public SearchResponseDTO(List<SearchResultDTO> mascotas, List<SearchResultDTO> propietarios, List<SearchResultDTO> historiales) {
        this.mascotas = mascotas;
        this.propietarios = propietarios;
        this.historiales = historiales;
    }

    public List<SearchResultDTO> getMascotas() {
        return mascotas;
    }

    public void setMascotas(List<SearchResultDTO> mascotas) {
        this.mascotas = mascotas;
    }

    public List<SearchResultDTO> getPropietarios() {
        return propietarios;
    }

    public void setPropietarios(List<SearchResultDTO> propietarios) {
        this.propietarios = propietarios;
    }

    public List<SearchResultDTO> getHistoriales() {
        return historiales;
    }

    public void setHistoriales(List<SearchResultDTO> historiales) {
        this.historiales = historiales;
    }
}

