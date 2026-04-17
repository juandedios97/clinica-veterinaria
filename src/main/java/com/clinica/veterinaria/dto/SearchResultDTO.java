package com.clinica.veterinaria.dto;

public class SearchResultDTO {
    private Long id;
    private String type; // "mascota", "propietario", "historial"
    private String titulo;
    private String subtitulo;
    private String url;

    public SearchResultDTO() {
    }

    public SearchResultDTO(Long id, String type, String titulo, String subtitulo, String url) {
        this.id = id;
        this.type = type;
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public void setSubtitulo(String subtitulo) {
        this.subtitulo = subtitulo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

