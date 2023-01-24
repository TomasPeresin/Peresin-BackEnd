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
    
}
