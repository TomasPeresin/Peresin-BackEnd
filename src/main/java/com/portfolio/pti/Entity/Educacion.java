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
public class Educacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @NotNull
    @Size(min = 1, max = 50, message = "no cumple con la longitud")
    private String institucionE;
    
    @NotNull
    @Size(min = 1, max = 50, message = "no cumple con la longitud")
    private String tituloE;
    
    @NotNull
    @Size(min = 1, max = 50, message = "no cumple con la longitud")
    private String estadoE;

    @NotNull
    @Size(min = 10, max = 10, message = "no cumple la longiutd")
    private LocalDate fechaInicio;
    
    @NotNull
    @Size(min = 10, max = 10, message = "no cumple la longiutd")
    private LocalDate fechaFin;
    
    public Educacion() {
    }

    public Educacion(String institucion, String titulo, String estado, LocalDate fechaIni, LocalDate fechaFin) {
        this.institucionE = institucion;
        this.tituloE = titulo;
        this.estadoE = estado;
        this.fechaInicio = fechaIni;
        this.fechaFin = fechaFin;
    }
    
}
