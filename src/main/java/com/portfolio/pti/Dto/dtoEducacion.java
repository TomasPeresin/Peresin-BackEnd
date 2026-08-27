package com.portfolio.pti.Dto;

import javax.validation.constraints.NotBlank;

public class dtoEducacion {
    @NotBlank
    private String institucionE;
    
    @NotBlank
    private String tituloE;
    
    @NotBlank
    private String estadoE;

    @NotBlank
    private Integer fechaInicio;
    
    @NotBlank
    private Integer fechaFin;

    public dtoEducacion() {
    }

    public dtoEducacion(String institucion, String titulo, String estado, Integer fechaIni, Integer fechaFin) {
        this.institucionE = institucion;
        this.tituloE = titulo;
        this.estadoE = estado;
        this.fechaInicio = fechaIni;
        this.fechaFin = fechaFin;
    }

    public String getInstitucionE() {
        return institucionE;
    }

    public void setInstitucionE(String institucionE) {
        this.institucionE = institucionE;
    }

    public String getTituloE() {
        return tituloE;
    }

    public void setTituloE(String tituloE) {
        this.tituloE = tituloE;
    }

    public String getEstadoE() {
        return estadoE;
    }

    public void setEstadoE(String estadoE) {
        this.estadoE = estadoE;
    }

    public Integer getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Integer fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Integer getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Integer fechaFin) {
        this.fechaFin = fechaFin;
    }
}
