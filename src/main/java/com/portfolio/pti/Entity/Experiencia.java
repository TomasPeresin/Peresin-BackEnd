package com.portfolio.pti.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Experiencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @NotNull
    @Size(min = 1, max = 150, message = "no cumple la longitud")
    private String nombreE;
    
    @NotNull
    @Size(min = 1, max = 2000, message = "no cumple la longitud")
    @javax.persistence.Column(length = 2000)
    private String descripcionE;
    
    @NotNull
    private Integer fechaInicio;
    
    @NotNull
    private Integer fechaFin;

    @Size(max = 500)
    @javax.persistence.Column(length = 500)
    private String tecnologias;
    
    //Constructores

    public Experiencia() {
    }

    public Experiencia(String nombreE, String descripcionE, Integer fechaIni, Integer fechaFin) {
        this.nombreE = nombreE;
        this.descripcionE = descripcionE;
        this.fechaInicio = fechaIni;
        this.fechaFin = fechaFin;
    }

    public Experiencia(String nombreE, String descripcionE, Integer fechaIni, Integer fechaFin, String tecnologias) {
        this.nombreE = nombreE;
        this.descripcionE = descripcionE;
        this.fechaInicio = fechaIni;
        this.fechaFin = fechaFin;
        this.tecnologias = tecnologias;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreE() {
        return nombreE;
    }

    public void setNombreE(String nombreE) {
        this.nombreE = nombreE;
    }

    public String getDescripcionE() {
        return descripcionE;
    }

    public void setDescripcionE(String descripcionE) {
        this.descripcionE = descripcionE;
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

    public String getTecnologias() {
        return tecnologias;
    }

    public void setTecnologias(String tecnologias) {
        this.tecnologias = tecnologias;
    }
}
