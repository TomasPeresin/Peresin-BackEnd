/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.portfolio.pti.Dto;

import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class dtoEducacion {
    @NotBlank
    private String institucionE;
    
    @NotBlank
    private String tituloE;
    
    @NotBlank
    private String estadoE;

    @NotBlank
    private LocalDate fechaInicio;
    
    @NotBlank
    private LocalDate fechaFin;

    public dtoEducacion() {
    }

    public dtoEducacion(String institucion, String titulo, String estado, LocalDate fechaIni, LocalDate fechaFin) {
        this.institucionE = institucion;
        this.tituloE = titulo;
        this.estadoE = estado;
        this.fechaInicio = fechaIni;
        this.fechaFin = fechaFin;
    }
    
}
