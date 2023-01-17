/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.portfolio.pti.Entity;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
public class Experiencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @NotNull
    @Size(min = 1, max = 50, message = "no cumple la longiutd")
    private String nombreE;
    
    @NotNull
    @Size(min = 1, max = 50, message = "no cumple la longiutd")
    private String descripcionE;
    
    @NotNull
    @Size(min = 10, max = 10, message = "no cumple la longiutd")
    private LocalDate fechaInicio;
    
    @NotNull
    @Size(min = 10, max = 10, message = "no cumple la longiutd")
    private LocalDate fechaFin;
    
    //Constructores

    public Experiencia() {
    }

    public Experiencia(String nombreE, String descripcionE, LocalDate fechaIni, LocalDate fechaFin) {
        this.nombreE = nombreE;
        this.descripcionE = descripcionE;
        this.fechaInicio = fechaIni;
        this.fechaFin = fechaFin;
    }
}
